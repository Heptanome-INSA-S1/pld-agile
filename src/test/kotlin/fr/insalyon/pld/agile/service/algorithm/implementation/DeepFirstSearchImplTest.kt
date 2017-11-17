package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.model.Junction
import fr.insalyon.pld.agile.service.algorithm.api.DeepFirstSearch
import org.junit.Assert.assertEquals
import org.junit.Test

class DeepFirstSearchImplTest {

  val source = Intersection(1L,0,0)

  val graph by lazy {
    val node2 = Intersection(2L,0,0)
    val node3 = Intersection(3L,0,0)
    val node4 = Intersection(4L,0,0)
    val node5 = Intersection(5L,0,0)
    val node6 = Intersection(6L,0,0)

    val roadOfLength1 = Junction(1, "")
    val roadOfLength2 = Junction(2, "")
    val roadOfLength4 = Junction(4, "")

    Graph(
        setOf<Intersection>(source, node2, node3, node4, node5, node6),
        setOf(
            Triple(source, roadOfLength1, node2),
            Triple(source, roadOfLength4, node3),
            Triple(node2, roadOfLength1, node4),
            Triple(node2, roadOfLength2, node5),
            Triple(node4, roadOfLength1, node3),
            Triple(node4, roadOfLength2, node5),
            Triple(node5, roadOfLength1, node6),
            Triple(node6, roadOfLength1, node2)
        )
    )
  }

  @Test
  fun getTree() {
    val dfs: DeepFirstSearch<Intersection, Junction> = DeepFirstSearchImpl(graph, source)
    val tree: Path<Intersection, Junction> = dfs.tree

    assertEquals(6, tree.nodes.size)

  }

}