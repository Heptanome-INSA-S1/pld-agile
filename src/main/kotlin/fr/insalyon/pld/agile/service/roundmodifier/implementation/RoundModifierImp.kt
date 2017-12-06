package fr.insalyon.pld.agile.service.roundmodifier.implementation

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.POSITIVE_INFINITY
import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.implementation.Dijkstra
import fr.insalyon.pld.agile.service.roundmodifier.api.RoundModifier
import fr.insalyon.pld.agile.service.roundvalidator.implementation.RoundValidatorImp
import org.omg.CORBA.DynAnyPackage.InvalidValue
import kotlin.math.max

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

            prevShortestPathDuration = prevShortestPath.toDuration(Config.DEFAULT_SPEED)
            nextShortestPathDuration = nextShortestPath.toDuration(Config.DEFAULT_SPEED)

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
        throw IllegalStateException("Cannot add this delivery to the round")
      }

    } else {
      throw IllegalArgumentException("Check the values for the given delivery.")
    }
  }

  fun <E> Iterable<E>.toLinkedHashSet(): LinkedHashSet<E> {
    val linkedHashSet = LinkedHashSet<E>()

    for (e in this) {
      linkedHashSet.add(e)
    }
    return linkedHashSet
  }

  override fun removeDelivery(i: Int, round: Round, speed: Speed) {

    var dijsktra: Dijkstra<Intersection, Junction>
    if (i != 0) {
      dijsktra = Dijkstra(plan, round.deliveries().elementAt(i - 1).address)
    } else {
      dijsktra = Dijkstra(plan, round.warehouse.address)
    }

    val path: Path<Intersection, Junction>
    if (i != round.deliveries().size - 1) {
      path = dijsktra.getShortestPath(round.deliveries().elementAt(i + 1).address)
    } else {
      path = dijsktra.getShortestPath(round.warehouse.address)
    }
    val durationOfPath = path.toDuration(speed)
    round.removeDelivery(round.deliveries().elementAt(i), path, durationOfPath)
  }

  override fun modifyDelivery(delivery: Delivery, round: Round, i: Int) {
    val listEarliestEnd = getEarliestEndTime(round)
    val listLatestEnd = getLastestEndTime(round)

    if (isDeliveryValid(delivery)) {
      if (delivery.startTime != null && delivery.startTime < listEarliestEnd[i] + round.durationPathInSeconds().elementAt(i).length.seconds) {
        throw  IllegalArgumentException("The start time is not valid and must be greater than " + (listEarliestEnd[i] + round.durationPathInSeconds().elementAt(i).length.seconds).toFormattedString())
      }

      var nextNonNullLatestEndTime: Instant? = getNextNonNull(listLatestEnd, i)
      if (delivery.startTime != null && nextNonNullLatestEndTime != null && delivery.startTime!! + delivery.duration > nextNonNullLatestEndTime!! - round.deliveries().elementAt(i + 1).duration.length.seconds - round.durationPathInSeconds().elementAt(i + 1).length.seconds) {
        throw IllegalArgumentException("The sum of the start time and the duration cannot exceed " + (nextNonNullLatestEndTime!! - round.deliveries().elementAt(i + 1).duration.length.seconds
            - round.durationPathInSeconds().elementAt(i + 1).length.seconds))
      }

      if (delivery.endTime != null && listLatestEnd[i + 1] != null && i < round.deliveries().size - 1 && delivery.endTime > listLatestEnd[i + 1]!! - round.deliveries().elementAt(i + 1).duration.length.seconds
          - round.durationPathInSeconds().elementAt(i + 1).length.seconds) {

        throw IllegalArgumentException("The end time cannot exceed " + (listLatestEnd[i + 1]!! - round.deliveries().elementAt(i + 1).duration.length.seconds
            - round.durationPathInSeconds().elementAt(i + 1).length.seconds))
      }

      if (delivery.startTime == null && delivery.endTime == null) {
        nextNonNullLatestEndTime = getNextNonNull(listEarliestEnd, i)

        if (nextNonNullLatestEndTime != null && delivery.duration.toSeconds() > nextNonNullLatestEndTime!!.toSeconds() - listEarliestEnd[i].toSeconds()) {
          throw IllegalArgumentException("The duration cannot exceed " + (nextNonNullLatestEndTime.toSeconds() - listEarliestEnd[i].toSeconds()))
        }
      }

      round.modify(i, delivery.startTime, delivery.endTime, delivery.duration)
    } else {
      throw IllegalArgumentException("Check the values for the given delivery.")
    }
  }

  fun getLastestEndTime(round: Round): List<Instant?> {

    val result = mutableListOf<Instant?>()
    result.add(null)

    round.deliveries().reversed().forEachIndexed { i, delivery ->

      var index = round.deliveries().size - 1 - i
      if (result.first() == null) {
        result.add(0, delivery.endTime)
      } else {
        var lastestDeparture = result.first()!! - (round.deliveries().elementAt(index + 1).duration + round.durationPathInSeconds().elementAt(index + 1).length.seconds)
        if (delivery.endTime != null && delivery.endTime < lastestDeparture) result.add(0, delivery.endTime) else result.add(0, lastestDeparture)
      }
    }
    return result
  }

  fun getEarliestEndTime(round: Round): List<Instant> {

    val result = mutableListOf<Instant>()
    result += round.warehouse.departureHour

    round.deliveries().forEachIndexed { index, delivery ->
      val arrivalTime = result[index] + round.durationPathInSeconds().elementAt(index).length.seconds
      val startDelivery = if (delivery.startTime != null && delivery.startTime > arrivalTime) delivery.startTime else arrivalTime
      result += startDelivery + delivery.duration
    }

    val arrivalTime = result.last() + round.durationPathInSeconds().last().length.seconds
    result += arrivalTime

    return result

  }

  private fun getNextNonNull(list: List<Instant?>, i: Int): Instant? {
    for (j: Int in i + 1 until list.size) {
      if (list[j] != null) {
        return list[j]
      }
    }
    return null
  }
/*
    /**
     * @param from : the delivery from the one you want to compute the time
     * @param to : the next delivery after from
     * @param round : the round you have to consider
     *
     * @return  the travelling time between the two given deliveries
     */
    private fun computeTravellingTime(from: Delivery, to: Delivery, round: Round): Int {
        var res = 0
        val index = round.deliveries().indexOf(to)
        return round.durationPathInSeconds().elementAt(index).toSeconds()
    }
*/

/*
  /**
   * @param from : the warehouse from which you want to compute the time
   * @param to : the next delivery after from
   * @param round : the round you have to consider
   *
   * @return  the travelling time between the two given deliveries
   */
  private fun computeTravellingTime(from: Warehouse, to: Delivery, round: Round): Int {
    var res = 0
    return round.durationPathInSeconds().first().toSeconds()
  }
*/

  /*
  /**
   * @param from : the delivery from the one you want to compute the time
   * @param to : the warehouse you have to reach after from
   * @param round : the round you have to consider
   *
   * @return  the travelling time between the two given deliveries
   */
  private fun computeTravellingTime(from: Delivery, to: Warehouse, round: Round): Int {
    var res = 0
    return round.durationPathInSeconds().last().toSeconds()
  }
*/
  private fun isDeliveryValid(delivery: Delivery): Boolean {
    if (delivery.startTime != null && delivery.endTime != null)
      if (delivery.startTime!! + delivery.duration > delivery.endTime) return false
    return true
  }

}

