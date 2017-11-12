package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.lib.graph.model.Edge
import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.service.algorithm.api.Dijkstra
import java.util.*

class DijsktraImpl<Node, out E : Measurable>(
    private val graph: Graph<Node, E>,
    override val source: Node,
    visitor: ((Node) -> Unit)? = null
) : Dijkstra<Node, E> {

  private val internalSourceNode = graph.nodes.first { n -> n.element == source }
  private val parent = MutableList<Int?>(graph.nodes.size, { _ -> null })
  private val parentEdge = MutableList<Edge<Node, E>?>(graph.nodes.size, { _ -> null })

  init {
    if (visitor != null) {
      computeWithVisitor(visitor)
    } else {
      compute()
    }
  }

  private fun compute() {
    parent[internalSourceNode.id] = internalSourceNode.id
    parent[internalSourceNode.id] = null

    val threaten = Array<Boolean>(graph.nodes.size, { _ ->
      false
    })
    val d = Array<Double>(graph.nodes.size, { _ -> Double.POSITIVE_INFINITY })
    d[internalSourceNode.id] = .0

    val nextNodes = PriorityQueue<Pair<Double, Int>>(kotlin.Comparator { o1, o2 -> o1.first.compareTo(o2.first) })
    nextNodes.add(Pair(.0, internalSourceNode.id))
    threaten[internalSourceNode.id] = true

    while (nextNodes.isNotEmpty()) {
      val currentNode = nextNodes.peek().second

      threaten[currentNode] = true
      nextNodes.poll()

      for (n in graph.outEdges[currentNode]) {
        val neighbour = n.to.id
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

  private fun computeWithVisitor(visitor: (Node) -> Unit) {
    parent[internalSourceNode.id] = internalSourceNode.id
    parent[internalSourceNode.id] = null

    val threaten = Array<Boolean>(graph.nodes.size, { _ ->
      false
    })
    val d = Array<Double>(graph.nodes.size, { _ -> Double.POSITIVE_INFINITY })
    d[internalSourceNode.id] = .0

    val nextNodes = PriorityQueue<Pair<Double, Int>>(kotlin.Comparator { o1, o2 -> o1.first.compareTo(o2.first) })
    nextNodes.add(Pair(.0, internalSourceNode.id))
    threaten[internalSourceNode.id] = true

    while (nextNodes.isNotEmpty()) {
      val currentNode = nextNodes.peek().second

      visitor.invoke(graph.nodes[currentNode].element)

      threaten[currentNode] = true
      nextNodes.poll()

      for (n in graph.outEdges[currentNode]) {
        val neighbour = n.to.id
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

  override fun getShortestPathNodes(destination: Node): List<Node> {
    val nodeIndex: Int = graph.nodes.find { n -> n.element == destination }!!.id

    var current: Int? = nodeIndex
    var previous: Int? = parent[nodeIndex]

    val path = mutableListOf<Node>()
    while (previous != null && previous != current) {

      val p = graph.nodes[previous]
      path.add(0, p.element)
      current = previous
      previous = parent[p.id]

    }

    if (path.isNotEmpty()) {
      path.add(graph.nodes[nodeIndex].element)
    }

    return path

  }

  override fun getShortestPathEdges(destination: Node): List<E> {
    val nodeIndex: Int = graph.nodes.find { n -> n.element == destination }!!.id

    var previous: Edge<Node, E>? = parentEdge[nodeIndex]

    val path = mutableListOf<E>()
    while (previous != null) {

      path.add(0, previous.element)
      previous = parentEdge[previous.from.id]

    }

    return path

  }

  override fun getShortestPath(destination: Node): Path<Node, E> {
    val nodes = getShortestPathNodes(destination)
    val edges = getShortestPathEdges(destination)
    return if (nodes.isEmpty()) Path.NO_WAY else Path(nodes, edges)
  }

}