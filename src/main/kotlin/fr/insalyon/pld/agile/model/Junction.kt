package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable

/**
 * A part of a road between two intersections
 */
class Junction(
    override val length: Long,
    val name: String
) : Measurable {

  override fun toString(): String {
    return "Junction(length=$length, name='$name')"
  }
}