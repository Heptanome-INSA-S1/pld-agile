package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable

class Junction(
    override val length: Int,
    val name: String
) : Measurable {
}