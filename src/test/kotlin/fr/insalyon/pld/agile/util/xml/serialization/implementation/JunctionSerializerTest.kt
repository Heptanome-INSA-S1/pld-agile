package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.Junction
import fr.insalyon.pld.agile.model.XmlConfig
import fr.insalyon.pld.agile.util.xml.serialization.implementation.JunctionSerializer
import org.junit.Assert
import org.junit.Test
import org.w3c.dom.Element

import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import javax.xml.parsers.DocumentBuilderFactory

class JunctionSerializerTest {

  private val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()

  private val serialiser: LSSerializer by lazy {
    val lsImpl: DOMImplementationLS = document.implementation.getFeature("LS", "3.0") as DOMImplementationLS
    val serializer = lsImpl.createLSSerializer()
    serializer.domConfig.setParameter("xml-declaration", false)
    serializer
  }

  private val junctionSerializer: JunctionSerializer = JunctionSerializer(document)

  @Test
  fun serialize() {

    val junction = Junction(
        length = 10,
        name = "Rue A"
    )

    val junctionAsString = "<troncon longueur=\"10.0\" nomRue=\"Rue A\"/>"
    val junctionAsElement: Element = junctionSerializer.serialize(junction)
    Assert.assertEquals(junctionAsString, serialiser.writeToString(junctionAsElement))

  }

  @Test
  fun unserialize() {

    val junctionAsElement = document.createElement(XmlConfig.Junction.TAG)
    junctionAsElement.setAttribute(XmlConfig.Junction.NAME, "Rue A")
    junctionAsElement.setAttribute(XmlConfig.Junction.LENGTH, "518.0")

    val junction: Junction = junctionSerializer.unserialize(junctionAsElement)

    Assert.assertEquals("Rue A", junction.name)
    Assert.assertEquals(518, junction.length)

  }

}