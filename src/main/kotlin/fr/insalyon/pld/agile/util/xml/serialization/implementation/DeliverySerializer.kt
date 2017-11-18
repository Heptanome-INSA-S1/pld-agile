package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.util.xml.serialization.api.XmlSerializer
import org.w3c.dom.Document
import org.w3c.dom.Element

class DeliverySerializer(
    val document: Document,
    val intersections: Map<Long, Intersection>
) : XmlSerializer<Delivery> {
  override fun serialize(element: Delivery): Element {
    val result: Element = document.createElement(XmlConfig.Delivery.TAG)

    result.setAttribute(XmlConfig.Delivery.ADDRESS, element.address.id.toString())

    if(element.endTime != null) {
      result.setAttribute(XmlConfig.Delivery.END_TIME, "${element.endTime.hour}:${element.endTime.minutes}:${element.endTime.seconds}")
    }
    if(element.startTime != null) {
      result.setAttribute(XmlConfig.Delivery.START_TIME, "${element.startTime.hour}:${element.startTime.minutes}:${element.startTime.seconds}")
    }

    result.setAttribute(XmlConfig.Delivery.DURATION, "${element.duration.toSeconds()}")

    return result
  }

  override fun unserialize(element: Element): Delivery {
    val intersectionId = element.getAttribute(XmlConfig.Delivery.ADDRESS).toLong()
    val intersection = intersections[intersectionId]!!
    val startTime = element.getAttributeOrNull(XmlConfig.Delivery.START_TIME)?.toInstant()
    val endTime = element.getAttributeOrNull(XmlConfig.Delivery.END_TIME)?.toInstant()
    val duration = element.getAttribute(XmlConfig.Delivery.DURATION).toInt().seconds

    return Delivery(
        intersection,
        startTime,
        endTime,
        duration
    )
  }


  private fun Element.getAttributeOrNull(name: String): String? {
    val attribute = getAttribute(name)
    if(attribute.isNullOrBlank()) return null
    return attribute
  }

}