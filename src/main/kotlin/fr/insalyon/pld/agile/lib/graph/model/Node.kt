package fr.insalyon.pld.agile.lib.graph.model

/**
 * Create a node from referred to the element `element`
 */
class Node<out E>(
    val id: Int,
    val element: E
) {
  override fun toString(): String {
    return "Node(id=$id, element=$element)"
  }
}