package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.POSITIVE_INFINITY
import fr.insalyon.pld.agile.model.RoundRequest
import java.util.*

/**
 * Basic heuristic with time slot checking
 */
class TSP1WithTimeSlot(
    roundRequest: RoundRequest
) : TemplateTSPWithTimeSlot(roundRequest) {

  override fun bound(sommetCourant: Int, nonVus: ArrayList<Int>, cout: Array<IntArray>, duree: IntArray, minRow: IntArray, currentTime: Int, startTimes: IntArray, endTimes: IntArray, bestCost: Int): Int {

    for(nonVu in nonVus) {

      var arrivalTime = currentTime + cout[sommetCourant][nonVu]
      if(arrivalTime < startTimes[nonVu]) {
        arrivalTime = startTimes[nonVu]
      }

      if(arrivalTime + duree[nonVu] > endTimes[nonVu]) {
        return Int.POSITIVE_INFINITY
      }

    }
    return 0
  }

}