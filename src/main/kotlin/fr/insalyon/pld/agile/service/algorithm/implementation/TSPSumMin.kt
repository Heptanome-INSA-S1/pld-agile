package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.model.Instant
import fr.insalyon.pld.agile.model.RoundRequest
import java.util.*

class TSPSumMin(
    roundRequest: RoundRequest
) : TemplateTSPWithTimeSlot(
   roundRequest
) {
  override fun bound(sommetCourant: Int, nonVus: ArrayList<Int>, cout: Array<LongArray>, duree: LongArray, currentTime: Instant, startTimes: Array<Instant?>, endTimes: Array<Instant?>): Long {
    var sum = 0L
    cout!!.forEach { sum += it.min()!! }
    return sum
  }

  override fun iterator(sommetCrt: Int, nonVus: ArrayList<Int>, cout: Array<LongArray>, duree: LongArray): Iterator<Int> {
    val sortedNoReached = nonVus.sortedBy { cout[sommetCrt][it] }
    return IteratorSeq(sortedNoReached, sommetCrt)
  }
}