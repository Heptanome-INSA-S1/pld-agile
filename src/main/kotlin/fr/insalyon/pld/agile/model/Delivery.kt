package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable

/**
 * A Delivery is a delivery point
 */
data class Delivery(
    /**
     * The intersection where the delivery will be done
     */
    val address: Intersection,

    /**
     * The start of the time slot for the delivery
     */
    val startTime: Instant? = null,

    /**
     * The end of the time slot for the delivery
     */
    val endTime : Instant? = null,

    /**
     * The duration of the delivery
     */
    val duration: Duration
) : Measurable {
  override val length: Int
    get() = duration.toSeconds()
}