package fr.insalyon.pld.agile.model

class RoundRequest(
    val warehouse: Warehouse,
    val deliveries: Set<Delivery>
)