package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.service.algorithm.api.Kruskal
import org.junit.Assert.assertEquals
import org.junit.Test

class KruskalImplTest {

  @Test
  fun compute() {

    val matrix = arrayOf(
        intArrayOf(0, 3, 0, 0, 1),
        intArrayOf(0, 0, 5, 0, 4),
        intArrayOf(0, 0, 0, 2, 0),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 6, 7, 4)
    )

    val nodes = arrayListOf<Int>(0,1,2,3,4)

    val kruskal: Kruskal = KruskalImpl(nodes, matrix, 0)

    assertEquals(11, kruskal.getLength())


  }


}