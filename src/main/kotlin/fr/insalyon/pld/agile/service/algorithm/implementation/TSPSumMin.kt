package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.POSITIVE_INFINITY
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
  override fun bound(sommetCourant: Int, nonVus: ArrayList<Int>, cout: Array<IntArray>, duree: IntArray, currentTime: Int, startTimes: IntArray, endTimes: IntArray, bestCost: Int): Int {
    var sum = 0
    val indicies = listOf(sommetCourant) + nonVus
    for(i in indicies) {
      var minSum = Int.POSITIVE_INFINITY
      for(j in indicies) {
        if(cout[i][j] < minSum) minSum = cout[i][j]
      }
      sum += minSum
    }
    return sum + nonVus.sumBy { duree[it] }
  }
}