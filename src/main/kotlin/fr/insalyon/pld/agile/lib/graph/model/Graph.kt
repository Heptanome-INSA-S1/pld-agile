package fr.insalyon.pld.agile.lib.graph.model

import fr.insalyon.pld.agile.POSITIVE_INFINITY

/**
 * A graph with nodes `N` and edges `E`.
 * The edges are like Triple(fromNode, edge, toNode)
 */
open class Graph<N, out E : Measurable>(
    private val elements: Set<N> = emptySet(),
    private val edges: Set<Triple<N, E, N>> = emptySet(),
    private val worstLength: Int = Int.POSITIVE_INFINITY
) {
  private val internalOutEdges = mutableListOf<MutableList<Edge<N, E>>>()
  private val internalInEdges = mutableListOf<MutableList<Edge<N, E>>>()

  /**
   * The wrapped nodes in the graph
   */

  val nodes = List(elements.size, { i -> Node(i, elements.elementAt(i)) })

  /**
   * Return the list of outEdges of the graph. To get the outEdges of a graph
   * use graph.outEdges\[node.index\]
   */
  val outEdges: List<List<Edge<N, E>>> get() = internalOutEdges

  /**
   * Return the list of inEdges of the graph. To get the inEdges of a node use
   * graph.inEdges\[node.index\]
   */
  val inEdges: List<List<Edge<N, E>>> get() = internalInEdges

  /**
   * Return the adjacencyMatrix of the graph
   */
  val adjacencyMatrix: Array<IntArray> by lazy {

    val matrix = Array(nodes.size, { _ -> IntArray(nodes.size, { _ -> worstLength }) })

    for (i: Int in nodes.indices) {
      for(edge: Edge<N,E> in outEdges[i]) {
        val j: Int = edge.to.index
        matrix[i][j] = edge.element.length
      }
    }

    matrix

  }

  init {

    nodes.forEach {
      internalOutEdges.add(mutableListOf())
      internalInEdges.add(mutableListOf())
    }

    edges.forEach {

      val from = nodes.find { n -> n.element == it.first }!!
      val to = nodes.find { n -> n.element == it.third }!!

      val edgeElement = it.second
      val edge = Edge(from, edgeElement, to)

      internalOutEdges[from.index].add(edge)
      internalInEdges[to.index].add(edge)

    }

  }

  /**
   * Return the output adjacency list for the node `node`
   */
  fun outEdgesOf(node: N): List<Edge<N, E>> = outEdges[nodes.first { it.element == node }.index]

  /**
   * Returns the input adjacency list of the node `node`
   */
  fun inEdgesOf(node: N): List<Edge<N, E>> = inEdges[nodes.first { it.element == node }.index]

  /**
   * Returns an edge between the node `from` and `to` (this can be null). If they are different edges between the two nodes,
   * this function will return the first one.
   */
  fun edgeBetween(from: N, to: N): Edge<N, E>? = outEdges[nodes.first { it.element == from }.index].find { it.to.element == to }

  override fun toString(): String = "Graph(nodes=$nodes, internalOutEdges=$internalOutEdges)"

  /**
   * Create a copy of the graph with applying the coefficient to the length of all the edges.
   */
  fun rescale(coef: Number): Graph<N, Measurable> {
    return Graph(
        elements,
        edges.map {
          val path = object : Measurable {
            override val length: Int
              get() = (it.second.length * coef.toDouble()).toInt()
          }
          Triple(it.first, path, it.third) }.toSet(),
        worstLength
    )
  }

  /**
   * Return a copy of the graph where all the edges are reversed. Ex: {A,B,C}, {A->B, B->C} will return {A,B,C} {B->A, C->B}
   */
  fun reverse(): Graph<N, E> {
    return Graph(
            elements,
            edges.map { Triple(it.third, it.second, it.first) }.toSet(),
            worstLength
    )
  }

}
