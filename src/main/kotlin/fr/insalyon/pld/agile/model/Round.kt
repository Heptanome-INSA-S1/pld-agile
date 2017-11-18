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
    path: LinkedHashSet<Path<Intersection, Junction>>
) : Observable(), Measurable{

  private val _deliveries: MutableList<Delivery> = deliveries.toMutableList()
  val deliveries: LinkedHashSet<Delivery> = _deliveries.toLinkedHashSet()

  private val _path: MutableList<Path<Intersection, Junction>> = path.toMutableList()
  val path: LinkedHashSet<Path<Intersection, Junction>> = _path.toLinkedHashSet()

  fun addDelivery(subPath: SubPath) {

    val deliveryBefore = _deliveries.first { it.address == subPath.pathFromPreviousDelivery.nodes.first() }

    val index = _deliveries.addAfter(deliveryBefore, subPath.delivery)
    _path.add(index, subPath.pathToNextDelivery)
    _path.add(index, subPath.pathFromPreviousDelivery)

    setChanged()
    notifyObservers()
  }

  fun removeDelivery(delivery: Delivery, pathToReplaceWith: Path<Intersection, Junction>) {

    val index = _deliveries.indexOf(delivery)
    if(index == -1) throw InvalidArgumentException(arrayOf("$delivery was not found in the round"))
    _deliveries.removeAt(index)

    _path.removeAt(index)
    _path.removeAt(index)

    _path.add(index, pathToReplaceWith)

    setChanged()
    notifyObservers()
  }

  override val length: Int
    get() {
      var duration = Duration()

      val deliveryIterator = _deliveries.iterator()
      val pathIterator = _path.iterator()

      var previousDelivery: Delivery? = null
      var currentHour = warehouse.departureHour

      for(i: Int in _deliveries.indices) {
        val destination = deliveryIterator.next()!!
        val path = pathIterator.next()!!

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

      val pathToWarehouse = pathIterator.next()!!
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

  private fun <E> MutableList<E>.toLinkedHashSet(): LinkedHashSet<E> {
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