package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.service.algorithm.api.Kruskal
import fr.insalyon.pld.agile.service.algorithm.api.TSP
import java.util.*

class TSP2 : TemplateTSP(), TSP {
  override fun bound(sommetCourant: Int?, nonVus: ArrayList<Int>?, cout: Array<out IntArray>?, duree: IntArray?): Int {

    val kruskal: Kruskal = KruskalImpl(nonVus!!, cout!!)
    return kruskal.getLength()
  }

  override fun iterator(sommetCrt: Int?, nonVus: ArrayList<Int>?, cout: Array<out IntArray>?, duree: IntArray?): MutableIterator<Int> {
    return IteratorSeq(nonVus, sommetCrt!!)
  }

}