package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.model.Junction
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.XmlConfig
import fr.insalyon.pld.agile.util.xml.serialization.api.XmlSerializer
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

/**
 * XmlSerializer for Plan objects
 */
class PlanSerializer(
    private val document: Document,
    private val intersectionSerializer: IntersectionSerializer,
    private val junctionSerializer: JunctionSerializer
) : XmlSerializer<Plan> {

  override fun serialize(element: Plan): Element {
    val triplets = element.outEdges.map { outEdges -> outEdges.map { edge -> Triple(edge.from.element.id, edge.element, edge.to.element.id) } }.flatten()
    val result = document.createElement(XmlConfig.Map.TAG)
    element.nodes.forEach { node -> result.appendChild(intersectionSerializer.serialize(node.element)) }
    triplets.forEach { triplet ->
      val xmlJunction = junctionSerializer.serialize(triplet.second)
      xmlJunction.setAttribute(XmlConfig.Junction.FROM, triplet.first.toString())
      xmlJunction.setAttribute(XmlConfig.Junction.TO, triplet.third.toString())
      result.appendChild(xmlJunction)
    }
    return result
  }

  override fun unserialize(element: Element): Plan {
    val nodeFromId: MutableMap<Long, Intersection> = mutableMapOf<Long, Intersection>()
    val nodes: Set<Intersection> = element
        .getElementsByTagName(XmlConfig.Intersection.TAG)
        .map {
          val intersection = intersectionSerializer.unserialize(it as Element)
          nodeFromId[intersection.id] = intersection
          intersection
        }.toSet()
    val edges: Set<Triple<Intersection, Junction, Intersection>> = element
        .getElementsByTagName(XmlConfig.Junction.TAG)
        .map {
          val junction = junctionSerializer.unserialize(it as Element)
          val from: Intersection = nodeFromId[it.getAttribute(XmlConfig.Junction.FROM).toLong()]!!
          val to: Intersection = nodeFromId[it.getAttribute(XmlConfig.Junction.TO).toLong()]!!
          Triple(from,junction,to)
        }.toSet()
    return Plan(nodes, edges)
  }

  private fun <T> NodeList.map(transform: (Node) -> T): List<T> = List(length, { index -> transform(item(index)) })

}