package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.model.hours
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import java.util.*
import kotlin.math.max

/**
 * Abstract implementation of TSP with time slot constraint
 */
abstract class TemplateTSPWithTimeSlot(
    private val roundRequest: RoundRequest
) : TSP {

  protected var meilleureSolution: Array<Int?>? = null
  protected var coutMeilleureSolution: Int = 0
  protected var tempsLimiteAtteint: Boolean? = null

  override fun getLimitTimeReached(): Boolean? = tempsLimiteAtteint

  override fun findSolution(timeLimitInMs: Int, numberOfNodes: Int, coast: Array<IntArray>, durations: IntArray) {
    tempsLimiteAtteint = false
    coutMeilleureSolution = roundRequest.warehouseLatestArrival.toSeconds()
    meilleureSolution = arrayOfNulls(numberOfNodes)
    val nonVus = ArrayList<Int>()
    for (i in 1 until numberOfNodes) nonVus.add(i)
    val vus = ArrayList<Int>(numberOfNodes)
    vus.add(0) // le premier sommet visite est 0
    val unreachable = 24.hours.toSeconds()
    branchAndBound(0, nonVus, vus, 0, coast, durations, System.currentTimeMillis(), timeLimitInMs,
        roundRequest.warehouse.departureHour.toSeconds(),
        (listOf(roundRequest.warehouse.departureHour.toSeconds()) + roundRequest.deliveries.map { if (it.startTime == null) 0 else it.startTime.toSeconds() }).toIntArray(),
        (listOf(unreachable) + roundRequest.deliveries.map { if (it.endTime == null) unreachable else it.endTime.toSeconds() }).toIntArray())
  }

  override fun getBestSolution(i: Int): Int? {
    return if (meilleureSolution == null || i < 0 || i >= meilleureSolution!!.size) null else meilleureSolution!![i]
  }

  override fun getBestSolutionCoast(): Int {
    return coutMeilleureSolution
  }

  protected abstract fun bound(
      sommetCourant: Int,
      nonVus: ArrayList<Int>,
      cout: Array<IntArray>,
      duree: IntArray,
      currentTime: Int,
      startTimes: IntArray,
      endTimes: IntArray,
      bestCost: Int
  ): Int

  fun iterator(
      sommetCrt: Int,
      nonVus: ArrayList<Int>,
      cout: Array<IntArray>,
      duree: IntArray,
      currentTime: Int,
      startTimes: IntArray,
      endTimes: IntArray
  ): Iterator<Int> {
    return IteratorSeq(nonVus.sortedBy {
      max(currentTime + cout[sommetCrt][it], startTimes[it])
    }, sommetCrt)
  }


  internal open fun branchAndBound(
      sommetCrt: Int,
      nonVus: ArrayList<Int>,
      vus: ArrayList<Int>,
      coutVus: Int,
      cout: Array<IntArray>,
      duree: IntArray,
      tpsDebut: Long,
      tpsLimite: Int,
      currentTime: Int,
      startTimes: IntArray,
      endTimes: IntArray
  ) {
    var coutVus = coutVus
    if (System.currentTimeMillis() - tpsDebut > tpsLimite) {
      tempsLimiteAtteint = true
      return
    }
    if (nonVus.size == 0) { // tous les sommets ont ete visites
      coutVus += cout[sommetCrt][0]
      if (coutVus < coutMeilleureSolution) { // on a trouve une solution meilleure que meilleureSolution
        vus.toArray(meilleureSolution)
        coutMeilleureSolution = coutVus
      }
    } else if (coutVus + bound(sommetCrt, nonVus, cout, duree, currentTime, startTimes, endTimes, coutMeilleureSolution) < coutMeilleureSolution) {
      val it = iterator(sommetCrt, nonVus, cout, duree, currentTime, startTimes, endTimes)
      for (prochainSommet in it) {
        vus.add(prochainSommet)
        nonVus.remove(prochainSommet)
        var time = currentTime + cout[sommetCrt][prochainSommet]
        var waitingTime = 0
        if (time < startTimes[prochainSommet]) {
          waitingTime = startTimes[prochainSommet] - time
          time = startTimes[prochainSommet]
        }
        if (endTimes[prochainSommet] < time + duree[prochainSommet]) {
          // Do nothing
        } else {
          time += duree[prochainSommet]
          branchAndBound(prochainSommet, nonVus, vus, coutVus + cout[sommetCrt][prochainSommet] + waitingTime + duree[prochainSommet], cout, duree, tpsDebut, tpsLimite, time, startTimes, endTimes)
        }
        vus.remove(prochainSommet)
        nonVus.add(prochainSommet)
      }
    }
  }

}

