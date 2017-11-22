package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.model.Junction
import fr.insalyon.pld.agile.service.algorithm.api.Dijkstra
import org.junit.Assert.assertEquals
import org.junit.Test

class DijsktraImplTest {

  val source = Intersection(1,0,0)

  val graph by lazy {
    val node2 = Intersection(2,0,0)
    val node3 = Intersection(3,0,0)
    val node4 = Intersection(4,0,0)
    val node5 = Intersection(5,0,0)
    val node6 = Intersection(6,0,0)

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
  fun getPath() {

    val dijkstra: Dijkstra<Intersection, Junction> = DijsktraImpl<Intersection, Junction>(graph, source)
    assertEquals(1, dijkstra.getShortestPath(Intersection(id = 2)).length)
    assertEquals(3, dijkstra.getShortestPath(Intersection(id = 3)).length)
    assertEquals(4, dijkstra.getShortestPath(Intersection(id = 6)).length)

    val pathTo6 = dijkstra.getShortestPathNodes(Intersection(id = 6))
    assertEquals(listOf(
        Intersection(id = 1),
        Intersection(id = 2),
        Intersection(id = 5),
        Intersection(id = 6)
    ), pathTo6)

    val path = dijkstra.getShortestPath(Intersection(id = 6))
    assertEquals(3, path.edges.size)
    assertEquals(4, path.edges.sumBy { it.length })

  }

}