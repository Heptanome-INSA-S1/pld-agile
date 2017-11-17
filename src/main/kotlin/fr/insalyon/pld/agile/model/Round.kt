package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path

/**
 * A round is a computed round request
 */
class Round(
    val warehouse: Warehouse,
    val deliveries: LinkedHashSet<Delivery>,
    val path: LinkedHashSet<Path<Intersection, Junction>>
) : Measurable{
  override val length: Int
    get() {

      var duration = Duration()

      val deliveryIterator = deliveries.iterator()
      val pathIterator = path.iterator()

      var previousDelivery: Delivery? = null
      var currentHour = warehouse.departureHour

      for(i: Int in deliveries.indices) {
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
}