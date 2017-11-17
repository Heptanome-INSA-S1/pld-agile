package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import javax.xml.parsers.DocumentBuilderFactory

class DeliverySerializerTest {

  private val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
  private val deliverySerializer: DeliverySerializer = DeliverySerializer(document, mapOf(
      10L to Intersection(10L, 4, 8),
      12L to Intersection(12L, 9, 10)
  ))

  private val serialiser: LSSerializer by lazy {
    val lsImpl: DOMImplementationLS = document.implementation.getFeature("LS", "3.0") as DOMImplementationLS
    val serializer = lsImpl.createLSSerializer()
    serializer.domConfig.setParameter("xml-declaration", false)
    serializer
  }


  @Test
  fun serialize() {

    val deliveryAsString = "<livraison adresse=\"194605312\" debutPlage=\"10:0:0\" duree=\"900\" finPlage=\"12:0:0\"/>"
    val delivery = Delivery(
        Intersection(194605312L, 10, 15),
        10 H 0 M 0,
        12 H 0 M 0,
        900.seconds
    )
    val deliveryAsElement = deliverySerializer.serialize(delivery)

    assertEquals(deliveryAsString, serialiser.writeToString(deliveryAsElement))

  }

  @Test
  fun unserialize() {

    val deliveryAsElement = document.createElement(XmlConfig.Delivery.TAG)
    deliveryAsElement.setAttribute(XmlConfig.Delivery.ADDRESS, "10")
    deliveryAsElement.setAttribute(XmlConfig.Delivery.START_TIME, "10:8:3")
    deliveryAsElement.setAttribute(XmlConfig.Delivery.DURATION, "400")

    val delivery = deliverySerializer.unserialize(deliveryAsElement)

    assertEquals(10 H 8 M 3, delivery.startTime)
    assertNull(delivery.endTime)
    assertEquals(400.seconds, delivery.duration)

  }

}