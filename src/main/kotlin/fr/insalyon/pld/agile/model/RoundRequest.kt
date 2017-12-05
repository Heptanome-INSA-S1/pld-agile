package fr.insalyon.pld.agile.model

/**
 * A round request
 */
class RoundRequest(
    val warehouse: Warehouse,
    val deliveries: Set<Delivery>
) {
  val intersections by lazy { (listOf(warehouse.address) + deliveries.map { it.address }) }
  val durations by lazy { listOf(0.seconds) + deliveries.map { it.duration } }
}