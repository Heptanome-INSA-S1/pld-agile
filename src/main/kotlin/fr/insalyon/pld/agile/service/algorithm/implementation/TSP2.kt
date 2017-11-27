package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.model.Instant
import fr.insalyon.pld.agile.service.algorithm.api.Kruskal
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import java.util.*

class TSP2(
    private val departure: Instant? = null,
    private val startTimes: LongArray? = null,
    private val endTimes: LongArray? = null
): TemplateTSP(), TSP {

  override fun branchAndBound(sommetCrt: Int, nonVus: ArrayList<Int>?, vus: ArrayList<Int>?, coutVus: Long, cout: Array<out LongArray>?, duree: LongArray?, tpsDebut: Long, tpsLimite: Int) {
    if (System.currentTimeMillis() - tpsDebut > tpsLimite) {
      tempsLimiteAtteint = true;
      return;
    }
    var coutVus = coutVus
    if (nonVus!!.size == 0) { // tous les sommets ont ete visites
      coutVus += cout!![sommetCrt][0];
      if (coutVus < coutMeilleureSolution) { // on a trouve une solution meilleure que meilleureSolution
        vus!!.toArray(meilleureSolution);
        coutMeilleureSolution = coutVus;
      }
    } else if (coutVus + bound(sommetCrt, nonVus, cout, duree) < coutMeilleureSolution) {
      val it = iterator(sommetCrt, nonVus, cout, duree);
      while (it.hasNext()) {
        val prochainSommet = it.next();
        vus!!.add(prochainSommet);
        nonVus.remove(prochainSommet);
        branchAndBound(prochainSommet, nonVus, vus, coutVus + cout!![sommetCrt][prochainSommet] + duree!![prochainSommet], cout, duree, tpsDebut, tpsLimite);
        vus.remove(prochainSommet);
        nonVus.add(prochainSommet);
      }
    }
  }

  override fun iterator(sommetCrt: Int?, nonVus: ArrayList<Int>?, cout: Array<out LongArray>?, duree: LongArray?): MutableIterator<Int> {
    val sortedNoReached = nonVus!!.sortedBy { cout!![sommetCrt!!][it] }
    return IteratorSeq(sortedNoReached, sommetCrt!!)
  }

  override fun bound(sommetCourant: Int?, nonVus: ArrayList<Int>?, cout: Array<out LongArray>?, duree: LongArray?): Long {
    val startTimes = startTimes ?: LongArray(duree!!.size, { _ -> -1L })
    val endTimes = endTimes ?: LongArray(duree!!.size, { _ -> -1L })

    val coastWithTimeWindow: Array<out LongArray>
    if(departure == null) {
      coastWithTimeWindow = cout!!
    } else {
      coastWithTimeWindow = cout!!
    }
    val kruskal: Kruskal = KruskalImpl(nonVus!!, coastWithTimeWindow)
    return kruskal.getLength()
  }

}