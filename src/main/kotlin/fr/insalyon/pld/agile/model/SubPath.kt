package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path

class SubPath(
    val pathFromPreviousDelivery: Path<Intersection, Junction>,
    val durationFromPreviousDelivery: Duration,
    val delivery: Delivery,
    val pathToNextDelivery: Path<Intersection, Junction>,
    val durationToNextDelivery: Duration
)