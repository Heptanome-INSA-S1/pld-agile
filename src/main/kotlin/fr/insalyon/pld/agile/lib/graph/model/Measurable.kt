package fr.insalyon.pld.agile.lib.graph.model

/**
 * A measurable object has a length (Int)
 */
interface Measurable : Comparable<Measurable> {

  val length: Int

  override operator fun compareTo(other: Measurable): Int = length.compareTo(other.length)
}