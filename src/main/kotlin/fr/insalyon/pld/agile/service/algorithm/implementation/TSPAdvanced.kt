package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.POSITIVE_INFINITY
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.model.hours
import java.util.*
import kotlin.math.max

/**
 * Advanced TSP heuristic based on this paper : http://cs.indstate.edu/cpothineni/alg.pdf
 */
class TSPAdvanced(
    roundRequest: RoundRequest
) : TemplateTSPWithTimeSlot(roundRequest) {
  override fun bound(sommetCourant: Int, nonVus: ArrayList<Int>, cout: Array<IntArray>, duree: IntArray, currentTime: Int, startTimes: IntArray, endTimes: IntArray, bestCost: Int): Int {

    for(nonVu in nonVus) {
      val arrivalTime = max(currentTime + cout[sommetCourant][nonVu], startTimes[nonVu])
      if(arrivalTime + duree[nonVu] > endTimes[nonVu]) return Int.POSITIVE_INFINITY
    }
    return bound(sommetCourant, nonVus, cout, duree, bestCost)
  }

  private fun bound(sommetCrt: Int, nonVus: ArrayList<Int>, cout: Array<IntArray>, duree: IntArray, bestCost: Int): Int {

    val minsRow = IntArray(nonVus.size + 1)

    val indicies = listOf(sommetCrt) + nonVus

    var sumRow = 0
    for (i in indicies) {
      var minRow = Int.POSITIVE_INFINITY
      for (j in indicies) {
        if (cout[i][j] < minRow) minRow = cout[i][j]
      }
      minsRow[indicies.indexOf(i)] = minRow
      sumRow += minRow
    }

    var sumCol = 0
    for (j in indicies) {
      var minCol = Int.POSITIVE_INFINITY
      for (i in indicies) {
        if (cout[i][j] - minsRow[indicies.indexOf(i)] < minCol) {
          minCol = cout[i][j] - minsRow[indicies.indexOf(i)]
        }
      }
      sumCol += minCol
    }
    return sumRow + sumCol + nonVus.sumBy { duree[it] }

  }
}