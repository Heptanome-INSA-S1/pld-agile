package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.Config

/**
 * A round request
 */
class RoundRequest(
    val warehouse: Warehouse,
    val deliveries: Set<Delivery>,
    val warehouseLatestArrival: Instant = Config.Business.DEFAULT_END_DELIVERING
) {
  val intersections by lazy { (listOf(warehouse.address) + deliveries.map { it.address }) }
  val durations by lazy { listOf(0.seconds) + deliveries.map { it.duration } }
}