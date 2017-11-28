package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.model.Instant
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.model.seconds
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import java.util.*

abstract class TemplateTSPWithTimeSlot(
    private val roundRequest: RoundRequest
) : TSP {

  protected var meilleureSolution: Array<Int?>? = null
  protected var coutMeilleureSolution: Long = 0
  protected var tempsLimiteAtteint: Boolean? = null

  override fun getLimitTimeReached(): Boolean? = tempsLimiteAtteint

  override fun findSolution(timeLimitInMs: Int, numberOfNodes: Int, coast: Array<LongArray>, durations: LongArray) {
    tempsLimiteAtteint = false
    coutMeilleureSolution = Long.MAX_VALUE
    meilleureSolution = arrayOfNulls(numberOfNodes)
    val nonVus = ArrayList<Int>()
    for (i in 1 until numberOfNodes) nonVus.add(i)
    val vus = ArrayList<Int>(numberOfNodes)
    vus.add(0) // le premier sommet visite est 0
    branchAndBound(0, nonVus, vus, 0, coast, durations, System.currentTimeMillis(), timeLimitInMs,
        roundRequest.warehouse.departureHour,
        (listOf<Instant?>(roundRequest.warehouse.departureHour) + roundRequest.deliveries.map { it.startTime }).toTypedArray(),
        (listOf<Instant?>(null) + roundRequest.deliveries.map { it.endTime }).toTypedArray())
  }

  override fun getBestSolution(i: Int): Int? {
    return if (meilleureSolution == null || i < 0 || i >= meilleureSolution!!.size) null else meilleureSolution!![i]
  }

  override fun getBestSolutionCoast(): Long {
    return coutMeilleureSolution
  }

  protected abstract fun bound(
      sommetCourant: Int,
      nonVus: ArrayList<Int>,
      cout: Array<LongArray>,
      duree: LongArray,
      currentTime: Instant,
      startTimes: Array<Instant?>,
      endTimes: Array<Instant?>
  ): Long

  protected abstract fun iterator(
      sommetCrt: Int,
      nonVus: ArrayList<Int>,
      cout: Array<LongArray>,
      duree: LongArray
  ): Iterator<Int>

  internal open fun branchAndBound(
      sommetCrt: Int,
      nonVus: ArrayList<Int>,
      vus: ArrayList<Int>,
      coutVus: Long,
      cout: Array<LongArray>,
      duree: LongArray,
      tpsDebut: Long,
      tpsLimite: Int,
      currentTime: Instant,
      startTimes: Array<Instant?>,
      endTimes: Array<Instant?>
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
    } else if (coutVus + bound(sommetCrt, nonVus, cout, duree, currentTime, startTimes, endTimes) < coutMeilleureSolution) {
      val it = iterator(sommetCrt, nonVus, cout, duree)
      for(prochainSommet in it) {
        vus.add(prochainSommet)
        nonVus.remove(prochainSommet)
        var time = currentTime + cout[sommetCrt][prochainSommet].seconds
        var waitingTime = 0.seconds
        if(startTimes[prochainSommet] != null && currentTime < startTimes[prochainSommet]!!) {
          waitingTime = startTimes[prochainSommet]!! - time
          time = startTimes[prochainSommet]!!
        }
        if(endTimes[prochainSommet] != null && endTimes[prochainSommet]!! < time + duree[prochainSommet].seconds) {
          // Do nothing
        } else {
          time += duree[prochainSommet].seconds
          branchAndBound(prochainSommet, nonVus, vus, coutVus + cout[sommetCrt][prochainSommet] + waitingTime.toSeconds() + duree[prochainSommet], cout, duree, tpsDebut, tpsLimite, time, startTimes, endTimes)
          time -= duree[prochainSommet].seconds
        }
        vus.remove(prochainSommet)
        nonVus.add(prochainSommet)
      }
    }
  }

}

