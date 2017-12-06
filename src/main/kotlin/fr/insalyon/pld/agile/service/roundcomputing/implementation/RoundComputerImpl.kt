package fr.insalyon.pld.agile.service.roundcomputing.implementation

import fr.insalyon.pld.agile.benchmark
import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import fr.insalyon.pld.agile.service.algorithm.implementation.Dijkstra
import fr.insalyon.pld.agile.service.roundcomputing.api.RoundComputer
import fr.insalyon.pld.agile.util.Logger
import java.util.*

/**
 * Implantation of the RoundComputer interface
 */
class RoundComputerImpl(
    /**
     * The plan
     */
    val plan: Plan,
    /**
     * The round request to compute
     */
    private val roundRequest: RoundRequest,
    private val tsp: TSP,
    /**
     * The truck speed
     */
    private val speed: Speed
) : RoundComputer {

  companion object {
    /**
     * Create a complete graph with the only intersections which are linked to the roundRequest
     */
    fun getSubPlan(plan: Plan, roundRequest: RoundRequest): Graph<Intersection, Path<Intersection, Junction>> {
      val nodes = mutableSetOf<Intersection>()
      val roads = mutableSetOf<Triple<Intersection, Path<Intersection, Junction>, Intersection>>()

      for (source: Intersection in roundRequest.intersections) {
        val dijsktra = Dijkstra<Intersection, Junction>(plan, source)
        val destinations = roundRequest.intersections.filter { it != source }
        for (destination: Intersection in destinations) {
          nodes.add(source)
          nodes.add(destination)
          roads.add(Triple(source, dijsktra.getShortestPath(destination), destination))
        }
      }
      return Graph(nodes, roads)
    }
  }

  private fun compute(): Round {
    val subPlanInMeters = getSubPlan(plan, roundRequest)
    val subPlanInSeconds = subPlanInMeters.rescale(1.0 / speed.to(Speed.DistanceUnit.M, Speed.DurationUnit.S).value)

    Logger.debug("TSP: " + benchmark {
      tsp.findSolution(
          10.minutes.toMillis().toInt(),
          roundRequest.intersections.size,
          subPlanInSeconds.adjacencyMatrix,
          roundRequest.durations.map { it.toSeconds() }.toIntArray()
      )
    }.first + "ms")

    val intersections = buildIntersections(tsp)
    val linkedSetOfDeliveries = buildDeliveries(intersections)
    val linkedSetOfDurationPaths = buildDurationPath(intersections, subPlanInSeconds)
    val linkedSetOfDistancePaths = buildDistancePath(intersections, subPlanInMeters)

    return Round(roundRequest.warehouse, linkedSetOfDeliveries, linkedSetOfDurationPaths, linkedSetOfDistancePaths)

  }

  private fun buildIntersections(tsp: TSP): List<Intersection> {
    val result = mutableListOf<Intersection>()
    roundRequest.intersections.indices
        .map { index -> tsp.getBestSolution(index) ?: throw RuntimeException("Cannot compute this round.") }
        .forEach { nodeIndex -> result += roundRequest.intersections[nodeIndex] }
    return result
  }

  private fun buildDeliveries(intersections: List<Intersection>): LinkedHashSet<Delivery> {
    return intersections.filterIndexed { i, _ -> i != 0 }.map { intersection -> roundRequest.deliveries.first { it.address == intersection } }.toLinkedHashSet()
  }

  private fun buildDistancePath(intersections: List<Intersection>, subPlan: Graph<Intersection, Path<Intersection, Junction>>): List<Path<Intersection, Junction>> {
    val result = mutableListOf<Path<Intersection, Junction>>()
    result.add(subPlan.edgeBetween(roundRequest.warehouse.address, intersections[1])!!.element)


    for (i in 1 until intersections.size - 1) {
      result.add(
          subPlan.edgeBetween(intersections[i], intersections[i + 1])!!.element
      )
    }

    result.add(subPlan.outEdgesOf(intersections.last()).find { it.to.element == roundRequest.warehouse.address }!!.element)
    return result
  }

  private fun buildDurationPath(intersections: List<Intersection>, subPlan: Graph<Intersection, Measurable>): List<Measurable> {
    val result = mutableListOf<Measurable>()

    result += subPlan.edgeBetween(roundRequest.warehouse.address, intersections[1])!!.element

    for (i in 1 until intersections.size - 1) {
      result.add(
          subPlan.edgeBetween(intersections[i], intersections[i + 1])!!.element
      )
    }

    result.add(subPlan.outEdgesOf(intersections.last()).find { it.to.element == roundRequest.warehouse.address }!!.element)
    return result
  }

  fun <E> List<E>.toLinkedHashSet(): LinkedHashSet<E> {
    val linkedHashSet = linkedSetOf<E>()
    this.forEach { linkedHashSet.add(it) }
    return linkedHashSet
  }

  override val round: Round
    get() = compute()

}