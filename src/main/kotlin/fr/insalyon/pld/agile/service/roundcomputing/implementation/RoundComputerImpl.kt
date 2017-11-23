package fr.insalyon.pld.agile.service.roundcomputing.implementation

import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import fr.insalyon.pld.agile.service.algorithm.implementation.DijsktraImpl
import fr.insalyon.pld.agile.service.algorithm.implementation.TSP1
import fr.insalyon.pld.agile.service.roundcomputing.api.RoundComputer

class RoundComputerImpl(
    /**
     * The plan
     */
    val plan: Plan,
    /**
     * The round request to compute
     */
    val roundRequest: RoundRequest,
    /**
     * The truck speed in dm/s
     */
    val speed: Speed
) : RoundComputer {

  private fun getSubPlan(): Graph<Intersection, Path<Intersection, Junction>> {
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

  private fun compute(): Round {

    val speedInDamSeconds = speed.to(Speed.DistanceUnit.DAM, Speed.DurationUnit.S)

    val tsp: TSP = TSP1()
    val subPlan = getSubPlan()
    tsp.findSolution(
        1000,
        roundRequest.intersections.size,
        subPlan.adjacencyMatrix.map { row -> row.map { it -> it * speedInDamSeconds.value }.toIntArray() }.toTypedArray(),
        roundRequest.durations.map { it.toSeconds() }.toIntArray()
    )



    val intersections = mutableListOf<Intersection>()
    for(i: Int in roundRequest.intersections.indices) {
      val currentNode = tsp.getBestSolution(i)
      intersections += roundRequest.intersections[currentNode]
    }

    val linkedSetOfDeliveries = linkedSetOf<Delivery>()
    val linkedSetOfPaths = linkedSetOf<Path<Intersection, Junction>>()

    fun addPath(from: Int, to: Int) {
      linkedSetOfPaths.add(
          subPlan.outEdgesOf(intersections[from])
              .first { it.to.element == intersections[to] }
              .element
      )
    }

    fun addDelivery(delivery: Int) {
      linkedSetOfDeliveries.add(
          roundRequest.deliveries.first { it.address == intersections[delivery] }
      )
    }

    linkedSetOfPaths.add(
        subPlan
            .outEdgesOf(roundRequest.warehouse.address)
            .first{ it.to.element == intersections[1]}
            .element
    )

    for(i in 1 until intersections.size - 1) {
      addDelivery(i)
      addPath(i, i + 1)
    }

    linkedSetOfDeliveries.add(
        roundRequest.deliveries.first { it.address == intersections.last() }
    )

    linkedSetOfPaths.add(
        subPlan
            .outEdgesOf(roundRequest.deliveries.first { it.address == intersections.last() }.address)
            .first { it.to.element == roundRequest.warehouse.address }
            .element
    )

    return Round(roundRequest.warehouse, linkedSetOfDeliveries, linkedSetOfPaths)

  }

  override val round: Round
  get() = compute()

}