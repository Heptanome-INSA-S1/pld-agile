package fr.insalyon.pld.agile.model

/**
 * A round request
 */
class RoundRequest(
    val warehouse: Warehouse,
    val deliveries: Set<Delivery>
)