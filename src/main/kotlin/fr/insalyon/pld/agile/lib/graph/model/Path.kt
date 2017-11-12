package fr.insalyon.pld.agile.lib.graph.model

import fr.insalyon.pld.agile.POSITIVE_INFINITY

/**
 * A path from the nodes nodes.first() to the nodes.nodes.last() using all the intermediate nodes
 * Path is represented this way : nodes[0] edges[0] nodes[1] ... edges[n-1] nodes[\n] where n = nodes.size - 1
 * Empty path means the current way does not exist
 * @exception RuntimeException Will raise a RuntimeException on creation if the nodes size does match with edges size.
 */
class Path<out N, out E : Measurable>(
    val nodes: List<N>,
    val edges: List<E>
) : Measurable {

  companion object {
    val NO_WAY: Path<Nothing, Nothing>
      get() = Path(listOf(), listOf())
  }

  init {
    if (nodes.isNotEmpty() && edges.isNotEmpty() && nodes.size != edges.size + 1) {
      throw RuntimeException("Path size does not match (${nodes.size} nodes and ${edges.size} edges)")
    }
  }

  override val length: Int by lazy {
    if (edges.isEmpty()) Int.POSITIVE_INFINITY else edges.sumBy { it.length }
  }

  override fun toString(): String {
    if (nodes.isEmpty()) return "Unknown path"

    val stringBuilder = StringBuilder()
        .append("[")
    for (i in 0 until nodes.size - 1) {
      stringBuilder.append(nodes[i])
          .append("] ")
          .append(edges[i])
          .append(" [")
    }
    stringBuilder.append(nodes.last())
        .append("]")

    return stringBuilder.toString()
  }


}