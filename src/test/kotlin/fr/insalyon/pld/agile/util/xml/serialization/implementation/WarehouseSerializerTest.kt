package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.Instant
import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.model.Warehouse
import fr.insalyon.pld.agile.model.XmlConfig
import org.junit.Assert.assertEquals
import org.junit.Test
import org.w3c.dom.Element
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import javax.xml.parsers.DocumentBuilderFactory

class WarehouseSerializerTest {

  private val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
  private val warehouseSerializer: WarehouseSerializer = WarehouseSerializer(document, mapOf(
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
  fun testSerialize() {

    val warehouse: Warehouse = Warehouse(
      Intersection(10, 4, 20),
      Instant(10, 14, 47)
    )

    assertEquals("<entrepot adresse=\"10\" heureDepart=\"10:14:47\"/>", serialiser.writeToString(warehouseSerializer.serialize(warehouse)))

  }

  @Test
  fun testUnserialize() {

    val warehouse1AsElement: Element = document.createElement(XmlConfig.Warehouse.TAG)
    warehouse1AsElement.setAttribute(XmlConfig.Warehouse.ADDRESS, "10")
    warehouse1AsElement.setAttribute(XmlConfig.Warehouse.DEPARTURE_HOUR, "22:15:14")

    val warehouse2AsElement: Element = document.createElement(XmlConfig.Warehouse.TAG)
    warehouse2AsElement.setAttribute(XmlConfig.Warehouse.ADDRESS, "12")
    warehouse2AsElement.setAttribute(XmlConfig.Warehouse.DEPARTURE_HOUR, "0:34:07")

    val warehouse1 = warehouseSerializer.unserialize(warehouse1AsElement)
    val warehouse2 = warehouseSerializer.unserialize(warehouse2AsElement)

    assertEquals(22, warehouse1.departureHour.hour)
    assertEquals(15, warehouse1.departureHour.minutes)
    assertEquals(14, warehouse1.departureHour.seconds)

    assertEquals(0, warehouse2.departureHour.hour)
    assertEquals(34, warehouse2.departureHour.minutes)
    assertEquals(7, warehouse2.departureHour.seconds)

  }

}