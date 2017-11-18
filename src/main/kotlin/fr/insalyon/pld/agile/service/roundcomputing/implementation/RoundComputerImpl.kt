package fr.insalyon.pld.agile.service.roundcomputing.implementation

import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import fr.insalyon.pld.agile.service.algorithm.implementation.DijsktraImpl
import fr.insalyon.pld.agile.service.algorithm.implementation.TSP1
import fr.insalyon.pld.agile.service.roundcomputing.api.RoundComputer

class RoundComputerImpl(
    val plan: Plan,
    val roundRequest: RoundRequest
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

    val tsp: TSP = TSP1()
    val subPlan = getSubPlan()
    tsp.findSolution(
        1000,
        roundRequest.intersections.size,
        subPlan.adjacencyMatrix,
        roundRequest.durations.map { it.toSeconds() }.toIntArray()
    )

    val intersections = mutableListOf<Intersection>()
    for(i: Int in roundRequest.intersections.indices) {
      val currentNode = tsp.getBestSolution(i)
      intersections += roundRequest.intersections[currentNode]
    }

    val linkedListOfDelivery = linkedSetOf<Delivery>()
    val linkedListOfPath = linkedSetOf<Path<Intersection, Junction>>()

    linkedListOfPath.add(
        subPlan
            .outEdgesOf(roundRequest.warehouse.address)
            .first{ it.to.element == intersections[1]}
            .element
    )

    for(i in 1 until intersections.size - 1) {

      linkedListOfDelivery.add(
          roundRequest.deliveries.first { it.address == intersections[i] }
      )

      linkedListOfPath.add(
          subPlan.outEdgesOf(intersections[i])
              .first { it.to.element == intersections[i+1] }
              .element
      )

    }

    linkedListOfDelivery.add(
        roundRequest.deliveries.last()
    )

    linkedListOfPath.add(
        subPlan
            .outEdgesOf(roundRequest.deliveries.last().address)
            .first { it.to.element == roundRequest.warehouse.address }
            .element
    )

    return Round(roundRequest.warehouse, linkedListOfDelivery, linkedListOfPath)

  }

  override val round: Round
  get() = compute()

}