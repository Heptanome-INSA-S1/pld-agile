package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.model.XmlConfig
import fr.insalyon.pld.agile.util.xml.serialization.api.XmlSerializer
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * XmlSerializer for Intersection objects
 */
class IntersectionSerializer(
    private val document: Document
) : XmlSerializer<Intersection> {

  override fun serialize(element: Intersection): Element {
    val result = document.createElement(XmlConfig.Intersection.TAG)
    result.setAttribute(XmlConfig.Intersection.ID, element.id.toString())
    result.setAttribute(XmlConfig.Intersection.X, element.x.toString())
    result.setAttribute(XmlConfig.Intersection.Y, element.y.toString())
    return result
  }

  override fun unserialize(element: Element): Intersection {
    return Intersection(
        element.getAttribute(XmlConfig.Intersection.ID).toInt(),
        element.getAttribute(XmlConfig.Intersection.X).toInt(),
        element.getAttribute(XmlConfig.Intersection.Y).toInt()
    )
  }
}