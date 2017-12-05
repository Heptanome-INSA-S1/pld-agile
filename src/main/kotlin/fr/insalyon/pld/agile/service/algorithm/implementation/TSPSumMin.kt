package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.POSITIVE_INFINITY
import fr.insalyon.pld.agile.model.Duration
import fr.insalyon.pld.agile.model.Instant
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.model.seconds
import fr.insalyon.pld.agile.util.Logger
import java.util.*
import kotlin.math.min

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

  override fun iterator(sommetCrt: Int, nonVus: ArrayList<Int>, cout: Array<IntArray>, duree: IntArray, currentTime: Int, startTimes: IntArray, endTimes: IntArray): Iterator<Int> {
    return IteratorSeq(nonVus.sortedBy { cout[sommetCrt][it] + duree[it] }, sommetCrt)
  }


}