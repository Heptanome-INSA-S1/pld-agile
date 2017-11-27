package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.POSITIVE_INFINITY
import fr.insalyon.pld.agile.service.algorithm.api.Kruskal

typealias Node = Int
typealias Length = Long
typealias Set = BooleanArray

class KruskalImpl(
    val nodes: ArrayList<Node>,
    edges: Array<out LongArray>,
    val noEdgeValue: Long = Long.POSITIVE_INFINITY
) : Kruskal {

  val edges = List<List<Triple<Node, Length, Node>>>(nodes.size, { departureIndex ->
    val outEdges = mutableListOf<Triple<Node, Length, Node>>()
    nodes
        .filter {  destinationIndex -> departureIndex != destinationIndex && edges[departureIndex][destinationIndex] != noEdgeValue }
        .forEach { destination -> outEdges += Triple(departureIndex, edges[departureIndex][nodes.indexOf(destination)], nodes.indexOf(destination)) }
    outEdges
  })

  private val parent = Array<Node?>(nodes.size, { _ -> null})
  private val rank = Array<Int>(nodes.size, { _ -> 0 })

  private fun find(node: Node): Node {
    var current: Node? = node
    while(parent[current!!] != null) current = parent[current]
    return current!!
  }

  private fun union(x: Node, y: Node) {

    val xRoot = find(x)
    val yRoot = find(y)

    if(xRoot != yRoot) {
      if(rank[xRoot] < rank[yRoot]) {
        parent[xRoot] = yRoot
      } else {
        parent[yRoot] = xRoot
        if(rank[xRoot] == rank[yRoot]) {
          rank[xRoot] += 1
        }
      }
    }

  }

  override fun getLength(): Long {

    val set = Set(nodes.size, { _ -> false})
    // Sort all the edges by length (it.second)
    val edges = edges.flatten().sortedBy { it.second }.iterator()
    var length = 0L

    // Create subset is already done

    edges.forEach { edge ->
      if(find(edge.first) != find(edge.third)) {
        length += edge.second
        union(edge.first, edge.third)
      }
    }

    return length

  }

  fun Set.isNotFull() = any { !it }

  fun Set.add(node: Node) {
    this[node] = true
  }

  operator fun Set.contains(node: Node): Boolean {
    return this[node]
  }

}