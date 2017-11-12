package fr.insalyon.pld.agile.service.algorithm.api

import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path

interface DeepFirstSearch<Node, out Edge : Measurable> {

  /**
   * Return the source node of the deep first search
   */
  val source: Node

  /**
   * Return the tree from the source to the others nodes (if a node is not in the tree, it's not reachable)
   */
  val tree: Path<Node, Edge>

  /**
   * Return the list of nodes that can be reached by the source
   */
  val treeNodes: List<Node>

  /**
   * Return the list of edges that can be reached by the source
   */
  val treeEdges: List<Edge>

}