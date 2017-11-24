package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.service.algorithm.api.Kruskal
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import java.util.*

class TSP2 : TemplateTSP(), TSP {

  override fun iterator(sommetCrt: Int?, nonVus: ArrayList<Int>?, cout: Array<out LongArray>?, duree: LongArray?): MutableIterator<Int> {
    return IteratorSeq(nonVus, sommetCrt!!)
  }

  override fun bound(sommetCourant: Int?, nonVus: ArrayList<Int>?, cout: Array<out LongArray>?, duree: LongArray?): Long {
    val kruskal: Kruskal = KruskalImpl(nonVus!!, cout!!)
    return kruskal.getLength()
  }

}