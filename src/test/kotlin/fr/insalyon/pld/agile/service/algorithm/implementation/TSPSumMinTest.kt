package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.POSITIVE_INFINITY
import fr.insalyon.pld.agile.model.*
import org.junit.Assert.*
import org.junit.Test

class TSPSumMinTest {

  @Test
  fun boundTest() {

    val roundRequest = RoundRequest(
        Warehouse(Intersection(1), 8 h 30),
        setOf(
            Delivery(Intersection(2), duration = 10.minutes),
            Delivery(Intersection(3), duration = 10.minutes),
            Delivery(Intersection(4), duration = 10.minutes),
            Delivery(Intersection(5), duration = 10.minutes)
        )
    )
    val tspSumMin = TSPSumMin(roundRequest)

    tspSumMin.findSolution(
        10.minutes.toMillis().toInt(),
        roundRequest.intersections.size,
        arrayOf(
            intArrayOf(Int.POSITIVE_INFINITY, 10, 8, 9, 7),
            intArrayOf(10, Int.POSITIVE_INFINITY, 10, 5, 6),
            intArrayOf(8, 10, Int.POSITIVE_INFINITY, 8, 9),
            intArrayOf(9, 5, 8, Int.POSITIVE_INFINITY, 6),
            intArrayOf(7, 6, 9, 6, Int.POSITIVE_INFINITY)
        ),
        roundRequest.durations.map { it.toSeconds() }.toIntArray()
    )

  }

}