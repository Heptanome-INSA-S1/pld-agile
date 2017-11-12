package fr.insalyon.pld.agile.lib.graph.model

import fr.insalyon.pld.agile.POSITIVE_INFINITY

/**
 * A graph with nodes `N` and edges `E`.
 * The edges are like Triple(fromNode, edge, toNode)
 */
open class Graph<N, out E : Measurable>(
    elements: Set<N> = emptySet(),
    edges: Set<Triple<N, E, N>> = emptySet(),
    worstLength: Int = Int.POSITIVE_INFINITY
) {
  private val internalOutEdges = mutableListOf<MutableList<Edge<N, E>>>()
  private val internalInEdges = mutableListOf<MutableList<Edge<N, E>>>()

  /**
   * The wrapped nodes in the graph
   */
  val nodes = List(elements.size, { i -> Node(i, elements.elementAt(i)) })

  /**
   * Return the list of outEdges of the graph. To get the outEdges of a graph
   * use graph.outEdges\[node.id\]
   */
  val outEdges: List<List<Edge<N, E>>> get() = internalOutEdges

  fun outEdgesOf(node: N): List<Edge<N, E>> {
    return outEdges[nodes.first { it.element == node }.id]
  }

  /**
   * Return the list of inEdges of the graph. To get the inEdges of a node use
   * graph.inEdges\[node.id\]
   */
  val inEdges: List<List<Edge<N, E>>> get() = internalInEdges

  fun inEdgesOf(node: N): List<Edge<N, E>> {
    return inEdges[nodes.first { it.element == node }.id]
  }

  /**
   * Return the adjacencyMatrix of the graph
   */
  val adjacencyMatrix: Array<IntArray> by lazy {

    val matrix = Array<IntArray>(nodes.size, { _ -> IntArray(nodes.size, { _ -> worstLength }) })

    for (i in nodes.indices) {
      for (j in nodes.indices) {
        val edge = inEdges[j].firstOrNull { edge -> edge.from.id == i }
        if (edge != null) {
          matrix[i][j] = edge.element.length
        }
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

      internalOutEdges[from.id].add(edge)
      internalInEdges[to.id].add(edge)

    }

  }

  override fun toString(): String {
    return "Graph(nodes=$nodes, internalOutEdges=$internalOutEdges)"
  }


}
