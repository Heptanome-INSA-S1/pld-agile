package fr.insalyon.pld.agile.model

import com.sun.javaws.exceptions.InvalidArgumentException
import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.sumLongBy
import java.util.*

/**
 * A round is a computed round request
 */
class Round(
    val warehouse: Warehouse,
    deliveries: LinkedHashSet<Delivery>,
    durationPath: LinkedHashSet<Measurable>,
    distancePath: LinkedHashSet<Path<Intersection, Junction>>
) : Observable(), Measurable{

  private val _deliveries: MutableList<Delivery> = deliveries.toMutableList()
  fun deliveries(): LinkedHashSet<Delivery> = _deliveries.toLinkedHashSet()

  private val _durationPath: MutableList<Measurable> = durationPath.toMutableList()
  fun durationPathInSeconds(): LinkedHashSet<Duration> {
    return _durationPath.map { it.length.seconds }.toLinkedHashSet()
  }

  private val _distancePath: MutableList<Path<Intersection, Junction>> = distancePath.toMutableList()
  fun distancePathInMeters(): LinkedHashSet<Path<Intersection, Junction>> {
    return _distancePath.toLinkedHashSet()
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
    _durationPath.add(index, subPath.pathToNextDelivery)
    _durationPath.add(index, subPath.pathFromPreviousDelivery)

    setChanged()
    notifyObservers()
  }

  fun modify(delivery: Delivery, startTime: Instant?, endTime: Instant?, duration: Duration) {
    val newDelivery = delivery.copy(startTime = startTime, endTime = endTime, duration = duration)
    val index = _deliveries.indexOf(delivery)
    _deliveries.removeAt(index)
    _deliveries.add(index, newDelivery)
  }

  fun removeDelivery(delivery: Delivery, pathToReplaceWith: Path<Intersection, Junction>) {

    val index = _deliveries.indexOf(delivery)
    if(index == -1) throw InvalidArgumentException(arrayOf("$delivery was not found in the round"))
    _deliveries.removeAt(index)

    _durationPath.removeAt(index)
    _durationPath.removeAt(index)

    _durationPath.add(index, pathToReplaceWith)

    setChanged()
    notifyObservers()
  }

  val distance: Long = _distancePath.sumLongBy { it.length }

  override val length: Long
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

  private fun <E> List<E>.toLinkedHashSet(): LinkedHashSet<E> {
    val linkedHashSet = linkedSetOf<E>()
    linkedHashSet.addAll(this)
    return linkedHashSet
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