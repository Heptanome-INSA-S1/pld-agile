package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Graph

class Plan(
    intersections: Set<Intersection>,
    junctions: Set<Triple<Intersection, Junction, Intersection>>
) : Graph<Intersection, Junction>(intersections, junctions)