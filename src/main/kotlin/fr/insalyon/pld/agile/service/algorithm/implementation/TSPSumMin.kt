package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.model.Duration
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
    cout!!.forEach { sum += it.min()!! }
    return sum
  }


  override fun iterator(sommetCrt: Int, nonVus: ArrayList<Int>, cout: Array<LongArray>, duree: LongArray, currentTime: Instant, startTimes: Array<Instant?>): Iterator<Int> {
    return IteratorSeq(nonVus.sortedBy { duration(currentTime, cout[sommetCrt][it].seconds, startTimes[it]) }, sommetCrt)
  }

  private fun duration(departureTime: Instant, journeryDuration: Duration, startTime: Instant?): Duration {

    val arrivalTime = departureTime + journeryDuration
    return if(startTime != null && startTime > arrivalTime) {
      journeryDuration + (startTime - arrivalTime)
    } else {
      journeryDuration
    }

  }

}