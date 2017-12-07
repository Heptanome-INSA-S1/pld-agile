package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.model.RoundRequest
import java.util.*

/**
 * TSP heuristic that add the `k` minimal edges where `k` is the minimum edges to link all the nonVus nodes
 */
class TSPSumMin(
    roundRequest: RoundRequest
) : TemplateTSPWithTimeSlot(
   roundRequest
) {
  override fun bound(sommetCourant: Int, nonVus: ArrayList<Int>, cout: Array<IntArray>, duree: IntArray, minRow: IntArray, currentTime: Int, startTimes: IntArray, endTimes: IntArray, bestCost: Int): Int {
    var sum = 0
    cout!!.forEach { sum += it.min()!! }
    return sum + nonVus.sumBy { duree[it] }
  }
}