package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Path

class SubPath(
    val pathFromPreviousDelivery: Path<Intersection, Junction>,
    val delivery: Delivery,
    val pathToNextDelivery: Path<Intersection, Junction>
)