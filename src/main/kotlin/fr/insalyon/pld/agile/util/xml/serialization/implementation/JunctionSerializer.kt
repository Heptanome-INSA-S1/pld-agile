package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.Junction
import fr.insalyon.pld.agile.model.XmlConfig
import fr.insalyon.pld.agile.util.xml.serialization.api.XmlSerializer
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * XmlSerializer for Junction objects
 */
class JunctionSerializer(
    private val document: Document
) : XmlSerializer<Junction> {

  override fun serialize(element: Junction): Element {
    val result = document.createElement(XmlConfig.Junction.TAG)
    result.setAttribute(XmlConfig.Junction.LENGTH, element.length.toString() + ".0")
    result.setAttribute(XmlConfig.Junction.NAME, element.name)
    return result
  }

  override fun unserialize(element: Element): Junction {
    return Junction(
        element.getAttribute(XmlConfig.Junction.LENGTH).toDouble().toLong(),
        element.getAttribute(XmlConfig.Junction.NAME)
    )
  }
}