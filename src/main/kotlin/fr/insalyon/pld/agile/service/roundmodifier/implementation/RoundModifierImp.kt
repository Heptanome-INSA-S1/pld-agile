package fr.insalyon.pld.agile.service.roundmodifier.implementation

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.POSITIVE_INFINITY
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.implementation.Dijkstra
import fr.insalyon.pld.agile.service.roundmodifier.api.RoundModifier
import fr.insalyon.pld.agile.service.roundvalidator.implementation.RoundValidatorImp
import fr.insalyon.pld.agile.util.max
import fr.insalyon.pld.agile.util.toLinkedHashSet

/**
 * Implementation of the round modifier interface
 */
class RoundModifierImp(
    private val plan: Plan
) : RoundModifier {

  override fun addDelivery(delivery: Delivery, round: Round) {
    if (isDeliveryValid(delivery)) {

      val dijsktraOnReversed: Dijkstra<Intersection, Junction> = Dijkstra(plan.reverse(), delivery.address)
      val dijsktra: Dijkstra<Intersection, Junction> = Dijkstra(plan, delivery.address)
      var prevShortestPath: Path<Intersection, Junction>
      var prevShortestPathDuration: Duration
      var nextShortestPath: Path<Intersection, Junction>
      var nextShortestPathDuration: Duration
      val listSubPath = mutableListOf<SubPath?>()

      val roundValidator = RoundValidatorImp()


      round.intersections()
          .filterIndexed { i, _ -> i < round.intersections().size - 1 }
          .forEachIndexed { i, _ ->

            prevShortestPath = dijsktraOnReversed.getShortestPath(round.intersections().elementAt(i)).reversed()
            nextShortestPath = dijsktra.getShortestPath(round.intersections().elementAt(i + 1))

            prevShortestPathDuration = prevShortestPath.toDuration(Config.Business.DEFAULT_SPEED)
            nextShortestPathDuration = nextShortestPath.toDuration(Config.Business.DEFAULT_SPEED)

            val roundCopy = Round(
                round.warehouse,
                round.deliveries().toLinkedHashSet(),
                round.durationPathInSeconds(),
                round.distancePathInMeters()
            )
            val currentSubPath = SubPath(prevShortestPath, prevShortestPathDuration, delivery, nextShortestPath, nextShortestPathDuration)
            roundCopy.addDelivery(currentSubPath)
            if (roundValidator.isValid(roundCopy)) {
              listSubPath.add(i, SubPath(prevShortestPath, prevShortestPathDuration, delivery, nextShortestPath, nextShortestPathDuration))
            } else {
              listSubPath.add(i, null)
            }
          }

      var bestPath: SubPath? = null
      var bestDuration = Int.POSITIVE_INFINITY
      var bestDistance = Int.POSITIVE_INFINITY
      for (i in listSubPath.indices) {
        if (listSubPath[i] != null) {

          val roundCopy = Round(round.warehouse, round.deliveries().toLinkedHashSet(), round.durationPathInSeconds(), round.distancePathInMeters())
          roundCopy.addDelivery(listSubPath[i]!!)

          if (roundCopy.length < bestDuration) {
            bestPath = listSubPath[i]
            bestDuration = roundCopy.length
            bestDistance = roundCopy.distance
          } else if (roundCopy.length == bestDuration && roundCopy.distance < bestDistance) {
            bestPath = listSubPath[i]
            bestDuration = roundCopy.length
            bestDistance = roundCopy.distance
          }
        }
      }

      if (bestPath != null) {
        round.addDelivery(bestPath)
      } else {
        throw IllegalStateException("Impossible d'ajouter cette livraison à la tournée")
      }

    } else {
      throw IllegalArgumentException("Nous n'avons pas le temps d'effectuer la livraison.")
    }
  }

  override fun removeDelivery(i: Int, round: Round, speed: Speed) {

    val dijsktra: Dijkstra<Intersection, Junction>
    dijsktra = if (i != 0) {
      Dijkstra(plan, round.deliveries().elementAt(i - 1).address)
    } else {
      Dijkstra(plan, round.warehouse.address)
    }

    val path: Path<Intersection, Junction>
    path = if (i != round.deliveries().size - 1) {
      dijsktra.getShortestPath(round.deliveries().elementAt(i + 1).address)
    } else {
      dijsktra.getShortestPath(round.warehouse.address)
    }
    val durationOfPath = path.toDuration(speed)
    round.removeDelivery(round.deliveries().elementAt(i), path, durationOfPath)
  }

  override fun modifyDelivery(delivery: Delivery, round: Round, i: Int) {

    val latestStartTime = round.getLatestArrivalTime()
    val earliestStartTime = round.getEarliestArrivalTimes()
    val earliestEndTime = round.getEarliestDepartureTime()


      if (delivery.startTime != null && delivery.startTime > latestStartTime[i]) {
        throw  IllegalArgumentException("L'heure de début n'est pas valide, elle doit être inférieure à ${latestStartTime[i].toFormattedString()}")
      }

      if (delivery.endTime != null && delivery.endTime < earliestEndTime[i] ){
        throw  IllegalArgumentException("L'heure de fin n'est pas valide, elle doit être supérieure à  ${earliestEndTime[i].toFormattedString()}")
      }

      if(delivery.startTime!= null && delivery.endTime!= null && delivery.endTime - delivery.startTime < delivery.duration) {
        throw IllegalArgumentException("Impossible d'effectuer ces modifications. Le créneau horaire fournit est plus court que la durée de livraison.")
      }

      val startDeliveryTime = max(earliestStartTime[i], delivery.startTime)
      if(startDeliveryTime + delivery.duration > latestStartTime[i+1]) {
        throw IllegalArgumentException("Impossible de repousser le début du créneau horaire. Durée maximum = ${round.getLastestDepartureTime()[i] - startDeliveryTime}")
      }
      round.modify(i, delivery.startTime, delivery.endTime, delivery.duration)

  }

  /*
   * Retourne la liste des plus grande heures de debut possible
   */

  fun getLatestStartTime(round: Round): List<Instant>{
    val waitingTime = round.getWaitingTimes()
    val result = mutableListOf<Instant>()
    var pathDuration = round.durationPathInSeconds()[0]
    var nextStartTime = round.warehouse.departureHour + pathDuration + waitingTime[0]
    result += nextStartTime - pathDuration

    round.deliveries().forEachIndexed { i, delivery ->
      pathDuration = round.durationPathInSeconds()[i+1]
      nextStartTime =if (i < waitingTime.size - 1)  nextStartTime + delivery.duration + pathDuration + waitingTime[i+1]  else Config.Business.DEFAULT_END_DELIVERING
      result += nextStartTime - pathDuration - delivery.duration
    }
    return result
  }

  /*
   * Retourne la liste des plus petite heures de fin possible
   */

  fun getEarliestEndTime(round: Round): List<Instant> {
    val result = mutableListOf<Instant>()
    val waitingTime = round.getWaitingTimes()
    var pathDuration = round.durationPathInSeconds()[0]
    var previousEndTime = round.warehouse.departureHour
    result += previousEndTime + pathDuration + round.deliveries()[0].duration

    round.deliveries().forEachIndexed { i, delivery ->
      previousEndTime += pathDuration + waitingTime[i] + delivery.duration
      pathDuration = round.durationPathInSeconds()[i+1]
      result += previousEndTime + pathDuration + if(i < round.deliveries().size - 1) round.deliveries()[i+1].duration else 0.seconds
    }

    return result
  }

  private fun isDeliveryValid(delivery: Delivery): Boolean {
    if (delivery.startTime != null && delivery.endTime != null)
      if (delivery.startTime!! + delivery.duration > delivery.endTime) return false
    return true
  }

}

