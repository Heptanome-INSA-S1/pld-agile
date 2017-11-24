package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.lib.graph.model.Edge
import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.service.algorithm.api.Dijkstra
import java.util.*

class DijsktraImpl<N, out E : Measurable>(
    private val graph: Graph<N, E>,
    override val source: N,
    visitor: ((N) -> Unit)? = null
) : Dijkstra<N, E> {

  private val internalSourceNode = graph.nodes.first { n -> n.element == source }
  private val parent = MutableList<Int?>(graph.nodes.size, { _ -> null })
  private val parentEdge = MutableList<Edge<N, E>?>(graph.nodes.size, { _ -> null })

  init {
    if (visitor != null) {
      computeWithVisitor(visitor)
    } else {
      compute()
    }
  }

  private fun compute() {
    parent[internalSourceNode.index] = internalSourceNode.index
    parent[internalSourceNode.index] = null

    val threaten = Array<Boolean>(graph.nodes.size, { _ ->
      false
    })
    val d = Array<Double>(graph.nodes.size, { _ -> Double.POSITIVE_INFINITY })
    d[internalSourceNode.index] = .0

    val nextNodes = PriorityQueue<Pair<Double, Int>>(kotlin.Comparator { o1, o2 -> o1.first.compareTo(o2.first) })
    nextNodes.add(Pair(.0, internalSourceNode.index))
    threaten[internalSourceNode.index] = true

    while (nextNodes.isNotEmpty()) {
      val currentNode = nextNodes.peek().second

      threaten[currentNode] = true
      nextNodes.poll()

      for (n in graph.outEdges[currentNode]) {
        val neighbour = n.to.index
        val edge = n.element.length
        if (threaten[neighbour]) {
          continue
        }

        if (d[neighbour] > d[currentNode] + edge) {
          d[neighbour] = d[currentNode] + edge
          nextNodes.add(Pair(d[neighbour], neighbour))
          parent[neighbour] = currentNode
          parentEdge[neighbour] = n
        }
      }
    }
  }

  private fun computeWithVisitor(visitor: (N) -> Unit) {
    parent[internalSourceNode.index] = internalSourceNode.index
    parent[internalSourceNode.index] = null

    val threaten = Array<Boolean>(graph.nodes.size, { _ ->
      false
    })
    val d = Array<Double>(graph.nodes.size, { _ -> Double.POSITIVE_INFINITY })
    d[internalSourceNode.index] = .0

    val nextNodes = PriorityQueue<Pair<Double, Int>>(kotlin.Comparator { o1, o2 -> o1.first.compareTo(o2.first) })
    nextNodes.add(Pair(.0, internalSourceNode.index))
    threaten[internalSourceNode.index] = true

    while (nextNodes.isNotEmpty()) {
      val currentNode = nextNodes.peek().second

      visitor(graph.nodes[currentNode].element)

      threaten[currentNode] = true
      nextNodes.poll()

      for (n in graph.outEdges[currentNode]) {
        val neighbour = n.to.index
        val edge = n.element.length
        if (threaten[neighbour]) {
          continue
        }

        if (d[neighbour] > d[currentNode] + edge) {
          d[neighbour] = d[currentNode] + edge
          nextNodes.add(Pair(d[neighbour], neighbour))
          parent[neighbour] = currentNode
          parentEdge[neighbour] = n
        }
      }
    }
  }

  override fun getShortestPathNodes(destination: N): List<N> {
    val nodeIndex: Int = graph.nodes.find { n -> n.element == destination }!!.index

    var current: Int? = nodeIndex
    var previous: Int? = parent[nodeIndex]

    val path = mutableListOf<N>()
    while (previous != null && previous != current) {

      val p = graph.nodes[previous]
      path.add(0, p.element)
      current = previous
      previous = parent[p.index]

    }

    if (path.isNotEmpty()) {
      path.add(graph.nodes[nodeIndex].element)
    }

    return path

  }

  override fun getShortestPathEdges(destination: N): List<E> {
    val nodeIndex: Int = graph.nodes.find { n -> n.element == destination }!!.index

    var previous: Edge<N, E>? = parentEdge[nodeIndex]

    val path = mutableListOf<E>()
    while (previous != null) {

      path.add(0, previous.element)
      previous = parentEdge[previous.from.index]

    }

    return path

  }

  override fun getShortestPath(destination: N): Path<N, E> {
    val nodes = getShortestPathNodes(destination)
    val edges = getShortestPathEdges(destination)
    return if (nodes.isEmpty()) Path.NO_WAY else Path(nodes, edges)
  }

}