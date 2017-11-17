package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.service.algorithm.api.DeepFirstSearch
import java.util.*

class DeepFirstSearchImpl<Node, out E : Measurable>(
    private val graph: Graph<Node, E>,
    override val source: Node,
    visitor: ((Node) -> Unit)? = null
) : DeepFirstSearch<Node, E> {

  private val internalSourceNode = graph.nodes.first { node -> node.element == source }
  private val internalTreeNodes = mutableListOf<Node>()
  private val internalTreeEdges = mutableListOf<E>()

  override val tree: Path<Node, E> by lazy {
    Path(internalTreeNodes, internalTreeEdges)
  }

  override val treeNodes: List<Node>
    get() = internalTreeNodes

  override val treeEdges: List<E>
    get() = internalTreeEdges

  init {
    if (visitor != null) {
      computeWithVisitor(visitor)
    } else {
      compute()
    }
  }

  private fun compute() {

    val stack = Stack<Int>()
    stack.push(internalSourceNode.index)

    val visited = Array<Boolean>(graph.nodes.size, { _ -> false })
    fun getNextUnvisitedChilds(nodeId: Int): Pair<Int, E>? {
      return graph.outEdges[nodeId]
          .filterNot { visited[it.to.index] }
          .sortedBy { it.element.length }
          .map { edge ->
            Pair(edge.to.index, edge.element)
          }.firstOrNull()
    }

    visited[internalSourceNode.index] = true
    internalTreeNodes.add(graph.nodes[internalSourceNode.index].element)
    while (stack.isNotEmpty()) {

      val currentNode = stack.peek()
      val child = getNextUnvisitedChilds(currentNode)
      if (child != null) {
        val childId = child.first
        val edge = child.second
        visited[childId] = true
        internalTreeNodes.add(graph.nodes[childId].element)
        internalTreeEdges.add(edge)
        stack.push(childId)
      } else {
        stack.pop()
      }

    }
  }

  private fun computeWithVisitor(visitor: (Node) -> Unit) {

    val stack = Stack<Int>()
    stack.push(internalSourceNode.index)
    visitor(internalSourceNode.element)
    val visited = Array<Boolean>(graph.nodes.size, { _ -> false })
    fun getNextUnvisitedChilds(nodeId: Int): Pair<Int, E>? {
      return graph.outEdges[nodeId]
          .filterNot { visited[it.to.index] }
          .sortedBy { it.element.length }
          .map { edge ->
            Pair(edge.to.index, edge.element)
          }.firstOrNull()
    }

    visited[internalSourceNode.index] = true
    internalTreeNodes.add(graph.nodes[internalSourceNode.index].element)
    while (stack.isNotEmpty()) {

      val currentNode = stack.peek()
      val child = getNextUnvisitedChilds(currentNode)
      if (child != null) {
        val childId = child.first
        val edge = child.second
        visited[childId] = true
        visitor(graph.nodes[childId].element)
        internalTreeNodes.add(graph.nodes[childId].element)
        internalTreeEdges.add(edge)
        stack.push(childId)
      } else {
        stack.pop()
      }

    }
  }


}