package fr.insalyon.pld.agile.service.algorithm.api

import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path

/**
 * Kruskal algorithm
 */
interface Kruskal {

  /**
   * Return the minimum spanning tree of the graph `graph`
   */
  fun <N, E : Measurable> getMinimumSpanningTree(graph: Graph<N, E>): Path<N, E>

  /**
   * Return the length of the minimum spanning tree
   */
  fun getLength(): Int
}