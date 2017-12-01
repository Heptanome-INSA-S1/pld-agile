package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.getResource
import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.serialization.implementation.*
import org.junit.Ignore
import org.junit.Test

class TspBenchmarkTest {

  fun getSubPlan(roundRequest: RoundRequest, plan: Plan): Graph<Intersection, Path<Intersection, Junction>> {
    val nodes = mutableSetOf<Intersection>()
    val roads = mutableSetOf<Triple<Intersection, Path<Intersection, Junction>, Intersection>>()

    for(source: Intersection in roundRequest.intersections) {
      val dijsktra = DijsktraImpl<Intersection, Junction>(plan, source)
      val destinations = roundRequest.intersections.filter { it != source }
      for(destination: Intersection in destinations) {
        nodes.add(source)
        nodes.add(destination)
        roads.add(Triple(source, dijsktra.getShortestPath(destination), destination))
      }
    }
    return Graph(nodes, roads)
  }


  @Ignore @Test
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

    val tsps = listOf<TSP>(TSP1(), TSPSumMin(roundRequest), TSPKruskal())

    val benchResults = mutableListOf<MutableList<Long>>()



    for (i in 15..15) {
      benchResults += mutableListOf<Long>()
      tsps.forEachIndexed { tspIndex, tsp ->
        val roundRequestTest = RoundRequest(
            roundRequest.warehouse,
            roundRequest.deliveries.filterIndexed { index, _ -> index <= i }.toSet()
        )

        val subPlan = getSubPlan(roundRequestTest, plan)

        val res = fr.insalyon.pld.agile.benchmark {
          tsp.findSolution(
              5.minutes.toMillis().toInt(),
              i,
              subPlan.adjacencyMatrix.map { row -> row.map { it -> (it * 15.km_h.to(Speed.DistanceUnit.DAM, Speed.DurationUnit.S).value).toLong() }.toLongArray() }.toTypedArray(),
              roundRequest.durations.map { it.toSeconds() }.toLongArray()
          )
        }
        benchResults.last() += res.first
        benchResults.last() += if(!tsp.limitTimeReached!!) 1L else 0L
      }
    }
    println("TSP 1  |Valid  |TSP Kru|Valid  |TSP SMi|Valid  ")
    benchResults.forEach {
      it.forEach { print(it.toString().fillLeft("       ") + "|") }
      println()
    }

  }

  private fun String.fillLeft(string: String): String {
    return string.substring(0, string.length - this.length) + this
  }


}