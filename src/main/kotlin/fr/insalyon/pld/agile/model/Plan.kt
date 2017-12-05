package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Graph

/**
 * A plan
 * The width will be the maximum x position minus the minimum x position
 * The height will be the maximum y position minus the minimum y position
 */
class Plan(
    intersections: Set<Intersection>,
    junctions: Set<Triple<Intersection, Junction, Intersection>>
) : Graph<Intersection, Junction>(intersections, junctions) {
  val width: Int by lazy { nodes.maxBy { it.element.x }!!.element.x - nodes.minBy { it.element.x }!!.element.x }
  val height: Int by lazy { nodes.maxBy { it.element.y }!!.element.y - nodes.minBy { it.element.y }!!.element.y }

  fun scale(x: Number): Plan {

    return Plan(
        intersections = nodes.map { it -> it.element }.toHashSet(),
        junctions = inEdges.map{ edges ->
          edges.map{
            val scaledEdge = Junction((x.toDouble() * it.element.length).toInt(), it.element.name)
            Triple(it.from.element, scaledEdge, it.to.element) }
        }.flatten().toSet()
    )

  }

}