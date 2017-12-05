package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.getResource
import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import fr.insalyon.pld.agile.service.roundcomputing.implementation.RoundComputerImpl
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.serialization.implementation.*
import org.junit.Ignore
import org.junit.Test

@Ignore
class TspBenchmarkTest {

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
  fun benchmark() {

    val xmlPlan = XmlDocument.open(getResource("xml/planLyonGrand.xml"))
    val intersectionSerializer = IntersectionSerializer(xmlPlan)
    val junctionSerializer = JunctionSerializer(xmlPlan)
    val planSerializer = PlanSerializer(xmlPlan, intersectionSerializer, junctionSerializer)
    val plan = planSerializer.unserialize(xmlPlan.documentElement)

    val xmlRoundRequest = XmlDocument.open(getResource("xml/DLgrand20.xml"))
    val warehouseSerializer = WarehouseSerializer(xmlRoundRequest, plan)
    val deliverySerializer = DeliverySerializer(xmlRoundRequest, plan)
    val roundRequestSerializer = RoundRequestSerializer(xmlRoundRequest, deliverySerializer, warehouseSerializer)
    val roundRequest = roundRequestSerializer.unserialize(xmlRoundRequest.documentElement)

    val tsps = listOf(TSPAdvanced(roundRequest)/*, TSPSumMin(roundRequest), TSPKruskal(roundRequest)*/)
    val tspAurore = TSP3()

    val subPlan = getSubPlan(roundRequest, plan)

    val timeSlot = Array(roundRequest.deliveries.size + 1, { _ -> intArrayOf(-1, -1)})
    for(i in roundRequest.deliveries.indices) {
      val start = roundRequest.deliveries.elementAt(i).startTime?.toSeconds() ?: -1
      val end = roundRequest.deliveries.elementAt(i).endTime?.toSeconds() ?: -1
      timeSlot[i+1] = intArrayOf(start, end)
    }

    for(i in 15..15) {

      val roundRequestTest = RoundRequest(
          roundRequest.warehouse,
          roundRequest.deliveries.filterIndexed { index, _ -> index <= i }.toSet()
      )

      val subPlan = getSubPlan(roundRequestTest, plan)

      val ms = fr.insalyon.pld.agile.benchmark {
        tspAurore.chercheSolution(
            10.minutes.toMillis().toInt(),
            roundRequestTest.intersections.size,
            subPlan.adjacencyMatrix.map { row -> row.map { it -> ((it.toDouble() / 15.km_h.to(Speed.DistanceUnit.M, Speed.DurationUnit.S).value)).toInt() }.toIntArray() }.toTypedArray(),
            roundRequestTest.durations.map { it.toSeconds() }.toIntArray(),
            timeSlot,
            roundRequestTest.warehouse.departureHour.toSeconds()
        )
      }
      print("Round size : ${i.toString().fillLeft("  ")} : ")
      println(ms.first.toString().fillLeft("              ") + "|")
    }

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