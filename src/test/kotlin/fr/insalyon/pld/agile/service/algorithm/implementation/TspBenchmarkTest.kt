package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.getResource
import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.util.toPlan
import fr.insalyon.pld.agile.util.toRoundRequest
import org.junit.Ignore
import org.junit.Test

@Ignore
class TspBenchmarkTest {

  val plan = getResource("xml/planLyonGrand.xml").toPlan()
  val roundRequest = getResource("xml/DLgrand20.xml").toRoundRequest(plan)

  fun getSubPlan(roundRequest: RoundRequest, plan: Plan): Graph<Intersection, Path<Intersection, Junction>> {
    val nodes = mutableSetOf<Intersection>()
    val roads = mutableSetOf<Triple<Intersection, Path<Intersection, Junction>, Intersection>>()

    for(source: Intersection in roundRequest.intersections) {
      val dijsktra = Dijkstra<Intersection, Junction>(plan, source)
      val destinations = roundRequest.intersections.filter { it != source }
      for(destination: Intersection in destinations) {
        nodes.add(source)
        nodes.add(destination)
        roads.add(Triple(source, dijsktra.getShortestPath(destination), destination))
      }
    }
    return Graph(nodes, roads)
  }


  @Test
  fun benchmarkTsp() {


    val tsps = listOf(TSPAdvanced(roundRequest), TSPSumMin(roundRequest)/*, TSP1WithTimeSlot(roundRequest)*/)

    for (i in 10..17) {
      print("Round size : ${i.toString().fillLeft("  ")} : ")
      tsps.forEachIndexed { tspIndex, tsp ->
        val roundRequestTest = RoundRequest(
            roundRequest.warehouse,
            roundRequest.deliveries.filterIndexed { index, _ -> index <= i }.toSet()
        )

        val subPlan = getSubPlan(roundRequestTest, plan)
        val res = fr.insalyon.pld.agile.benchmark {
          tsp.findSolution(
              20.minutes.toMillis().toInt(),
              i,
              subPlan.adjacencyMatrix.map { row -> row.map { it -> ((it.toDouble() / 15.km_h.to(Speed.DistanceUnit.M, Speed.DurationUnit.S).value)).toInt() }.toIntArray() }.toTypedArray(),
              roundRequest.durations.map { it.toSeconds() }.toIntArray()
          )
        }
        print(res.first.toString().fillLeft("              ") + "|")
        print((if(!tsp.limitTimeReached!!) 1L else 0L).toString().fillLeft("              ") + "|")
      }
      println()
    }

  }

  private fun String.fillLeft(string: String): String {
    return string.substring(0, string.length - this.length) + this
  }

}