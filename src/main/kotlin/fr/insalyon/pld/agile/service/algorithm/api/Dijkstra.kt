package fr.insalyon.pld.agile.service.algorithm.api

import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path

/**
 * DijkstraImpl algorithm. Compute the shortest path from a given point to 'to'
 */
interface Dijkstra<Node, out Edge : Measurable> {

  /**
   * Return the source of the dijkstra algorithm
   */
  val source: Node

  /**
   * Return all the points which are in the shortest path between source and destination
   */
  fun getShortestPathNodes(destination: Node): List<Node>

  /**
   * Return all the edges which are in the shortest path between source and destination
   */
  fun getShortestPathEdges(destination: Node): List<Edge>

  /**
   * Return the shortest path between source and destination
   */
  fun getShortestPath(destination: Node): Path<Node, Edge>

}