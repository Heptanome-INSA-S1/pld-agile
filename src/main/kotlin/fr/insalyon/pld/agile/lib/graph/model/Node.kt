package fr.insalyon.pld.agile.lib.graph.model

/**
 * Create a node from referred to the element `element`
 */
class Node<out E>(
    val index: Int,
    val element: E
) {
  override fun toString(): String = "Node(index=$index, element=$element)"
}