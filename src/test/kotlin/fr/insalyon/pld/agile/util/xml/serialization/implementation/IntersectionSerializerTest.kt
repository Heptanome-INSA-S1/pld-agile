package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.model.XmlConfig
import fr.insalyon.pld.agile.util.xml.serialization.implementation.IntersectionSerializer
import org.junit.Assert
import org.junit.Test

import org.w3c.dom.Element
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import javax.xml.parsers.DocumentBuilderFactory

class IntersectionSerializerTest {

  private val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()

  private val serialiser: LSSerializer by lazy {
    val lsImpl: DOMImplementationLS = document.implementation.getFeature("LS", "3.0") as DOMImplementationLS
    val serializer = lsImpl.createLSSerializer()
    serializer.domConfig.setParameter("xml-declaration", false)
    serializer
  }

  private val intersectionSerializer: IntersectionSerializer = IntersectionSerializer(document)

  @Test
  fun serialize() {

    val intersection = Intersection(
        id = 10,
        x = 1,
        y = 2
    )

    val intersection1ToString = "<noeud id=\"10\" x=\"1\" y=\"2\"/>"
    Assert.assertEquals(intersection1ToString, serialiser.writeToString(intersectionSerializer.serialize(intersection)))

  }

  @Test
  fun unserialize() {

    val element: Element = document.createElement(XmlConfig.Intersection.TAG)
    element.setAttribute(XmlConfig.Intersection.ID, "10")
    element.setAttribute(XmlConfig.Intersection.X, "2")
    element.setAttribute(XmlConfig.Intersection.Y, "4")

    val intersection = intersectionSerializer.unserialize(element)

    Assert.assertEquals(intersection.id, 10)
    Assert.assertEquals(intersection.x, 2)
    Assert.assertEquals(intersection.y, 4)

  }

}