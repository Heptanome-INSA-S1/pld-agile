package fr.insalyon.pld.agile.model

import com.sun.javaws.exceptions.InvalidArgumentException
import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path
import java.util.*

/**
 * A round is a computed round request
 */
class Round(
    val warehouse: Warehouse,
    deliveries: LinkedHashSet<Delivery>,
    durationPath: List<Measurable>,
    distancePath: List<Path<Intersection, Junction>>
) : Observable(), Measurable {

  private val _deliveries: MutableList<Delivery> = deliveries.toMutableList()
  /**
   * Return the ordered list of deliveries
   */
  fun deliveries(): List<Delivery> = _deliveries.toList()

  /**
   * Return the duration of the paths in the round
   */
  private val _durationPath: MutableList<Measurable> = durationPath.toMutableList()
  fun durationPathInSeconds(): List<Duration> {
    return _durationPath.map { it.length.seconds }
  }

  /**
   * Return the distance paths (plan part) in the round
   */
  private val _distancePath: MutableList<Path<Intersection, Junction>> = distancePath.toMutableList()
  fun distancePathInMeters(): List<Path<Intersection, Junction>> {
    return _distancePath
  }

  /**
   * Return the waitingTimes for the deliveries in the round
   */
  fun getWaitingTimes(): List<Duration> {
    var waitingTimes = mutableListOf<Duration>()
    var currentTime = warehouse.departureHour
    _deliveries.forEachIndexed { i, delivery ->
      currentTime += _durationPath[i].length.seconds
      if(delivery.startTime != null && delivery.startTime > currentTime) {
        waitingTimes.add(delivery.startTime - currentTime)
        currentTime  = delivery.startTime
      } else {
        waitingTimes.add(0.seconds)
      }
      currentTime += delivery.duration
    }
    return waitingTimes
  }

  fun addDelivery(subPath: SubPath) {

    val index: Int
    if(warehouse.address == subPath.pathFromPreviousDelivery.nodes.first()) {
      index = 0
      _deliveries.add(0, subPath.delivery)
    } else {
      val deliveryBefore = _deliveries.first { it.address == subPath.pathFromPreviousDelivery.nodes.first() }
      index = _deliveries.addAfter(deliveryBefore, subPath.delivery)
    }
    _durationPath.add(index, subPath.durationToNextDelivery)
    _durationPath.add(index, subPath.durationFromPreviousDelivery)

    _distancePath.add(index, subPath.pathToNextDelivery)
    _distancePath.add(index, subPath.pathFromPreviousDelivery)

    setChanged()
    notifyObservers()
  }

  fun removePath(i: Int) {
    _durationPath.removeAt(i)
    _distancePath.removeAt(i)
  }

  fun modify(index: Int, startTime: Instant?, endTime: Instant?, duration: Duration) {
    val newDelivery = _deliveries[index].copy(startTime = startTime, endTime = endTime, duration = duration)
    _deliveries[index] = newDelivery

    setChanged()
    notifyObservers()
  }

  fun removeDelivery(delivery: Delivery, pathToReplaceWith: Path<Intersection, Junction>, duration: Duration) {

    val index = _deliveries.indexOf(delivery)
    if(index == -1) throw InvalidArgumentException(arrayOf("$delivery was not found in the round"))

    _deliveries.removeAt(index)

    _durationPath.removeAt(index)
    _distancePath.removeAt(index)

    _durationPath.removeAt(index)
    _distancePath.removeAt(index)

    _durationPath.add(index, duration)
    _distancePath.add(index, pathToReplaceWith)

    setChanged()
    notifyObservers()
  }

  val distance: Int = _distancePath.sumBy { it.length }

  override val length: Int
    get() {
      var duration = Duration()

      val deliveryIterator = _deliveries.iterator()
      val pathIterator = _durationPath.iterator()

      var previousDelivery: Delivery? = null
      var currentHour = warehouse.departureHour

      for(i: Int in _deliveries.indices) {
        val destination = deliveryIterator.next()
        val path = pathIterator.next()

        if(previousDelivery != null) {
          duration += previousDelivery.duration
          currentHour += previousDelivery.duration
        }

        duration += path.length.seconds
        currentHour += path.length.seconds

        if(destination.startTime != null && currentHour < destination.startTime) {
          duration += destination.startTime - currentHour
          currentHour = destination.startTime
        }
        previousDelivery = destination

      }

      val pathToWarehouse = pathIterator.next()
      duration += previousDelivery!!.duration
      duration += pathToWarehouse.length.seconds

      return duration.toSeconds()
    }

  override fun toString(): String {
    val stringBuilder = StringBuilder()

    stringBuilder.append(warehouse.address.id)
        .append(" -> ")

    _deliveries.forEach{ stringBuilder.append(it.address.id).append(" -> ") }

    stringBuilder.append(warehouse.address.id)
    return stringBuilder.toString()
  }

  fun toTrace(): String {

    val stringBuilder = StringBuilder()

    stringBuilder.appendln("Departure: ${warehouse.departureHour}")
    stringBuilder.appendln("Warehouse at ${warehouse.address.id}")
    var time = warehouse.departureHour
    deliveries().forEachIndexed{index, delivery ->
      time += _durationPath[index].length.seconds
      var waitingTime = 0.seconds
      if(delivery.startTime != null && time < delivery.startTime) {
        waitingTime = delivery.startTime - time
      }

      stringBuilder.appendln("Travel time: ${_durationPath[index].length.seconds}")

      stringBuilder.appendln("Delivery: ${delivery.address.id}")
      stringBuilder.appendln("Arrival at $time - Waiting time: $waitingTime - StartTime: ${delivery.startTime} - Duration: ${delivery.duration} - EndTime: ${delivery.endTime} - DepartureTime: ${time + waitingTime + delivery.duration}")
      time += waitingTime
      time += delivery.duration
    }

    stringBuilder.appendln("Travel time: ${_durationPath.last().length.seconds}")
    time += _durationPath.last().length.seconds
    stringBuilder.appendln("Arrival at $time")
    stringBuilder.appendln("Warehouse at ${warehouse.address.id}")

    return stringBuilder.toString()

  }

  private fun <E> MutableList<E>.addAfter(elementBefore: E, elementToAdd: E): Int {
    val indexOfElementBefore = indexOf(elementBefore) + 1
    if(indexOfElementBefore == -1) {
      throw InvalidArgumentException(arrayOf("$elementBefore was not found in the round"))
    } else {
      add(indexOfElementBefore, elementToAdd)
    }
    return indexOfElementBefore
  }


}