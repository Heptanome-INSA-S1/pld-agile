package fr.insalyon.pld.agile.service.roundcomputing.implementation

import fr.insalyon.pld.agile.benchmark
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
    private val roundRequest: RoundRequest,
    private val tsp: TSP = TSP1(),
    /**
     * The truck speed
     */
    private val speed: Speed
) : RoundComputer {

  fun getSubPlan(): Graph<Intersection, Path<Intersection, Junction>> {
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

    val subPlan = getSubPlan()
    benchmark {
      tsp.findSolution(
          15.minutes.toMillis().toInt(),
          roundRequest.intersections.size,
          subPlan.adjacencyMatrix.map { row -> row.map { it -> (it * speedInDamSeconds.value).toLong() }.toLongArray() }.toTypedArray(),
          roundRequest.durations.map { it.toSeconds() }.toLongArray()
      )
    }

    val intersections = buildIntersections(tsp)
    val linkedSetOfDeliveries = buildDeliveries(intersections)
    val linkedSetOfPaths = buildPath(intersections, subPlan)

    return Round(roundRequest.warehouse, linkedSetOfDeliveries, linkedSetOfPaths)

  }

  private fun buildIntersections(tsp: TSP): List<Intersection> {
    val result = mutableListOf<Intersection>()
    roundRequest.intersections.indices
        .map { index -> tsp.getBestSolution(index) ?: throw RuntimeException("Cannot compute this round.") }
        .forEach { nodeIndex -> result += roundRequest.intersections[nodeIndex] }
    return result
  }

  private fun buildDeliveries(intersections: List<Intersection>): LinkedHashSet<Delivery> {
    return intersections.filterIndexed{ i, _ -> i != 0 }.map { intersection -> roundRequest.deliveries.first { it.address == intersection } }.toLinkedHashSet()
  }

  private fun buildPath(intersections: List<Intersection>, subPlan: Graph<Intersection, Path<Intersection, Junction>>): LinkedHashSet<Path<Intersection, Junction>> {
    val result = LinkedHashSet<Path<Intersection, Junction>>()
    result.add(subPlan.edgeBetween(roundRequest.warehouse.address, intersections[1])!!.element)

    for(i in 1 until intersections.size - 1) {
      result.add(
          subPlan.edgeBetween(intersections[i], intersections[i+1])!!.element
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