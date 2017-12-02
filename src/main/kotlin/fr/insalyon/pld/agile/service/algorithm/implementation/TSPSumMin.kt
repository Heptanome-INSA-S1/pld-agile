package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.model.Instant
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.model.seconds
import java.util.*

class TSPSumMin(
    roundRequest: RoundRequest
) : TemplateTSPWithTimeSlot(
   roundRequest
) {
  override fun bound(sommetCourant: Int, nonVus: ArrayList<Int>, cout: Array<LongArray>, duree: LongArray, currentTime: Instant, startTimes: Array<Instant?>, endTimes: Array<Instant?>): Long {
    var sum = 0L
    nonVus!!.forEach { totalDuration(sommetCourant, it, nonVus, cout, duree, currentTime, startTimes, endTimes) }
    return sum
  }

  override fun iterator(sommetCrt: Int, nonVus: ArrayList<Int>, cout: Array<LongArray>, duree: LongArray): Iterator<Int> {
    val sortedNoReached = nonVus.sortedBy { cout[sommetCrt][it] }
    return IteratorSeq(sortedNoReached, sommetCrt)
  }

  private fun totalDuration(sommetCourant: Int, nextNode: Int, nonVus: ArrayList<Int>, cout: Array<LongArray>, duree: LongArray, currentTime: Instant, startTimes: Array<Instant?>, endTimes: Array<Instant?>): Long {

    var time = currentTime + cout[sommetCourant][nextNode].seconds
    var totalDuration = cout[sommetCourant][nextNode].seconds

    if(startTimes[nextNode] != null && startTimes[nextNode]!! > time) {
      totalDuration += (startTimes[nextNode]!! - time)
      time = startTimes[nextNode]!!
    }

    time += duree[nextNode].seconds

    if(endTimes[nextNode] != null && endTimes[nextNode]!! < time) {
      return Long.MAX_VALUE
    }

    return totalDuration.toSeconds()

  }

}