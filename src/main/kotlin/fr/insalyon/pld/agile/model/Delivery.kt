package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable

data class Delivery(
    val address: Intersection,
    val startTime: Instant? = null,
    val endTime : Instant? = null,
    val duration: Duration
) : Measurable {
  override val length: Int
    get() = duration.toSeconds()
}