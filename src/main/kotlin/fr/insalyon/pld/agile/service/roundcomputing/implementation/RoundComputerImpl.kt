package fr.insalyon.pld.agile.service.roundcomputing.implementation

import fr.insalyon.pld.agile.benchmark
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import fr.insalyon.pld.agile.util.Logger

/**
 * Implantation of the RoundComputer interface
 */
class RoundComputerImpl(
    plan: Plan,
    roundRequest: RoundRequest,
    private val tsp: TSP,
    speed: Speed
) : RoundComputerTemplate(plan, roundRequest, speed) {

  override fun compute(): Round {
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

}