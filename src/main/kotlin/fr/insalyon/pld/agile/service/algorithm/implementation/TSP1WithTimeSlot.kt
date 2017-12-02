package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.model.Instant
import fr.insalyon.pld.agile.model.RoundRequest
import java.util.*

class TSP1WithTimeSlot(
    roundRequest: RoundRequest
) : TemplateTSPWithTimeSlot(roundRequest) {
  override fun iterator(sommetCrt: Int, nonVus: ArrayList<Int>, cout: Array<LongArray>, duree: LongArray, currentTime: Instant, startTimes: Array<Instant?>): Iterator<Int> {
    return IteratorSeq(nonVus, sommetCrt)
  }

  override fun bound(sommetCourant: Int, nonVus: ArrayList<Int>, cout: Array<LongArray>, duree: LongArray, currentTime: Instant, startTimes: Array<Instant?>, endTimes: Array<Instant?>): Long {
    return 0L
  }
}