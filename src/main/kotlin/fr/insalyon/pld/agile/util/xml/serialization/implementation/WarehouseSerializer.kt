package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.util.xml.serialization.api.XmlSerializer
import org.w3c.dom.Document
import org.w3c.dom.Element

class WarehouseSerializer(
    val document: Document,
    val plan: Plan
) : XmlSerializer<Warehouse> {

  override fun serialize(element: Warehouse): Element {
    val result: Element = document.createElement(XmlConfig.Warehouse.TAG)
    result.setAttribute(XmlConfig.Warehouse.ADDRESS, element.address.id.toString())
    val departureHour: Instant = element.departureHour
    result.setAttribute(XmlConfig.Warehouse.DEPARTURE_HOUR, "${departureHour.hour}:${departureHour.minutes}:${departureHour.seconds}")
    return result
  }

  override fun unserialize(element: Element): Warehouse {

    val intersectionId: Long = element.getAttribute(XmlConfig.Warehouse.ADDRESS).toLong()
    val intersection: Intersection = plan.nodes.first { it.element.id == intersectionId }.element
    val departureTime: Instant = element.getAttribute(XmlConfig.Warehouse.DEPARTURE_HOUR).toInstant()

    return Warehouse(
        intersection,
        departureTime
    )

  }
}