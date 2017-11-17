package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable
import fr.insalyon.pld.agile.lib.graph.model.Path
import java.util.*

class Round(
    val warehouse: Warehouse,
    val deliveries: SortedSet<Delivery>,
    val path: SortedSet<Path<Intersection, Junction>>
) : Measurable{
  override val length: Int
    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}