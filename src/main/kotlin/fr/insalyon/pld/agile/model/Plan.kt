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

fun String.toPlan(): Plan {

    var lines = trim().split("\n")
    val regex = "([0-9]+) ?-> ?([0-9]+) ?: ?([0-9]+)".toRegex()

    val speed = (lines[0].toSpeed().to(Speed.DistanceUnit.M, Speed.DurationUnit.M).value).toInt()
    lines = lines.map { it.replace(" ", "") }
    val points = lines[1].split(";").map { Intersection(it.toLong()) }.toSet()

    var edges = mutableSetOf<Triple<Intersection, Junction, Intersection>>()
    for(line in lines.filterIndexed{ i, _ -> i > 1}) {

        val regexResult = regex.matchEntire(line)!!.groupValues
        val from = points.first { it.id == regexResult[1].toLong() }
        val to = points.first { it.id == regexResult[2].toLong() }
        val length = regexResult[3].toInt()

        edges.add(Triple(from, Junction(length * speed, ""), to))
    }

    return Plan(
            points,
            edges
    )

}