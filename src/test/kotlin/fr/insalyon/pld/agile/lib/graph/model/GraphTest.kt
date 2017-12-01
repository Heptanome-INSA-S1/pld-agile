package fr.insalyon.pld.agile.lib.graph.model

import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.model.Junction
import org.junit.Assert.assertEquals
import org.junit.Test

class GraphTest {

  @Test
  fun rescale() {

    val nodeA = Intersection(0)
    val nodeB = Intersection(1)

    val junction = Junction(1000, "Rue A")

    val graph = Graph<Intersection, Junction>(setOf(nodeA, nodeB), setOf(Triple(nodeA, junction, nodeB)))
    var durationGraph = graph.rescale(10)
    assertEquals(10_000, durationGraph.outEdgesOf(nodeA)[0].element.length)
    durationGraph = graph.rescale(.5)
    assertEquals(500, durationGraph.outEdgesOf(nodeA)[0].element.length)
  }

}