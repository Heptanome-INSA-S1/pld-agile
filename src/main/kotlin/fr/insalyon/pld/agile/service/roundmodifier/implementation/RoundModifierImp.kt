package fr.insalyon.pld.agile.service.roundmodifier.implementation

import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.implementation.DijsktraImpl
import fr.insalyon.pld.agile.service.roundmodifier.api.RoundModifier
import org.omg.CORBA.DynAnyPackage.InvalidValue

class RoundModifierImp(
    private var round: Round,
    private val plan: Plan
) : RoundModifier {

  override fun addDelivery(delivery: Delivery, round: Round) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun removeDelivery(i: Int, round: Round, speed: Speed) {

    var dijsktra: DijsktraImpl<Intersection, Junction>
    if (i != 0)
      dijsktra = DijsktraImpl<Intersection, Junction>(plan, round.deliveries().elementAt(i - 1).address)
    else
      dijsktra = DijsktraImpl<Intersection, Junction>(plan, round.warehouse.address)

    if (i != round.deliveries().size - 1) {
      val path = dijsktra.getShortestPath(round.deliveries().elementAt(i + 1).address)
      round.removeDelivery(round.deliveries().elementAt(i), path, path.toDuration(speed))
    } else {
      val replacementPath = dijsktra.getShortestPath(round.warehouse.address)
      round.removeDelivery(round.deliveries().elementAt(i), replacementPath, replacementPath.toDuration(speed))
    }
  }

  override fun modifyDelivery(delivery: Delivery, round: Round, i: Int) {
    val listEarliestEnd = getEarliestEndTime(round)
    val listLatestEnd = getLastestEndTime(round)

    var isFirstPartValid = true
    var isSecondPartValid = true

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
    var nextNonNullLatestEndTime: Instant? = null
    for (j: Int in i + 1..list.size) {
      if (list[j] != null) {
        nextNonNullLatestEndTime = list[j]
        break
      }
    }
    return nextNonNullLatestEndTime
  }

  /**
   * @param from : the delivery from the one you want to compute the time
   * @param to : the next delivery after from
   * @param round : the round you have to consider
   *
   * @return  the travelling time between the two given deliveries
   */
  private fun computeTravellingTime(from: Delivery, to: Delivery, round: Round): Long {
    var res = 0L
    val index = round.deliveries().indexOf(to)
    return round.durationPathInSeconds().elementAt(index).toSeconds()
  }

  /**
   * @param from : the warehouse from which you want to compute the time
   * @param to : the next delivery after from
   * @param round : the round you have to consider
   *
   * @return  the travelling time between the two given deliveries
   */
  private fun computeTravellingTime(from: Warehouse, to: Delivery, round: Round): Long {
    var res = 0L
    return round.durationPathInSeconds().first().toSeconds()
  }

  /**
   * @param from : the delivery from the one you want to compute the time
   * @param to : the warehouse you have to reach after from
   * @param round : the round you have to consider
   *
   * @return  the travelling time between the two given deliveries
   */
  private fun computeTravellingTime(from: Delivery, to: Warehouse, round: Round): Long {
    var res = 0L
    return round.durationPathInSeconds().last().toSeconds()
  }

  private fun isDeliveryValid(delivery: Delivery): Boolean {
    if (delivery.startTime != null && delivery.endTime != null)
      if (delivery.startTime!! + delivery.duration > delivery.endTime) return false
    return true
  }

}

