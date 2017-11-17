package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Graph

class Plan(
    intersections: Set<Intersection>,
    junctions: Set<Triple<Intersection, Junction, Intersection>>
) : Graph<Intersection, Junction>(intersections, junctions) {
  val width: Int by lazy { nodes.maxBy { it.element.x }!!.element.x - nodes.minBy { it.element.x }!!.element.x }
  val height: Int by lazy { nodes.maxBy { it.element.y }!!.element.y - nodes.minBy { it.element.y }!!.element.y }
}