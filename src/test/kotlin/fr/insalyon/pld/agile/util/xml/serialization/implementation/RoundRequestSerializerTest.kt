package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.*
import org.junit.Assert
import org.junit.Test
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import javax.xml.parsers.DocumentBuilderFactory

class RoundRequestSerializerTest {

    private val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()

    private val plan = Plan(
            setOf(Intersection(25303798, 4, 20),Intersection(195279, 10, 15), Intersection(26464319, 10, 20)),
            setOf<Triple<Intersection, Junction, Intersection>>(

            )
    )

    private val deliverySerializer: DeliverySerializer = DeliverySerializer(document,plan)
    private val warehouseSerializer: WarehouseSerializer= WarehouseSerializer(document,plan)
    private val roundRequestSerializer: RoundRequestSerializer = RoundRequestSerializer(document,deliverySerializer,warehouseSerializer)

    private val serialiser: LSSerializer by lazy {
        val lsImpl: DOMImplementationLS = document.implementation.getFeature("LS", "3.0") as DOMImplementationLS
        val serializer = lsImpl.createLSSerializer()
        serializer.domConfig.setParameter("xml-declaration", false)
        serializer
    }

    @Test
    fun testSerialize() {

        val warehouse = Warehouse(
                Intersection(25303798, 4, 20),
                Instant(8, 0, 0)
        )

        val deliveryA = Delivery(
                Intersection(195279, 10, 15),
                null,
                null,
                900.seconds)
        val deliveryB = Delivery(
                Intersection(26464319, 10, 20),
                null,
                null,
                600.seconds)

        val deliveries: Set<Delivery> = setOf<Delivery>(
                deliveryA,
                deliveryB
        )

        val roundRequest = RoundRequest(
                warehouse,
                deliveries
        )

        val stringBuilder: StringBuilder = StringBuilder()
                .append("<demandeDeLivraisons>")
                .append("<entrepot adresse=\"25303798\" heureDepart=\"8:0:0\"/>")
                .append("<livraison adresse=\"195279\" duree=\"900\"/>")
                .append("<livraison adresse=\"26464319\" duree=\"600\"/>")
                .append("</demandeDeLivraisons>")

        val roundRequestAsString = stringBuilder.toString()

        Assert.assertEquals(roundRequestAsString, serialiser.writeToString(roundRequestSerializer.serialize(roundRequest)))

    }

    @Test
    fun testUnserialize() {

        val roundRequestAsElement = document.createElement(XmlConfig.RoundRequest.TAG)

        val warehouseAsElement = document.createElement(XmlConfig.Warehouse.TAG)
        warehouseAsElement.setAttribute(XmlConfig.Warehouse.ADDRESS, "25303798")
        warehouseAsElement.setAttribute(XmlConfig.Warehouse.DEPARTURE_HOUR, "8:0:0")

        val deliveryAAsElement = document.createElement(XmlConfig.Delivery.TAG)
        deliveryAAsElement.setAttribute(XmlConfig.Delivery.ADDRESS, "195279")
        deliveryAAsElement.setAttribute(XmlConfig.Delivery.DURATION, "900")

        val deliveryBAsElement = document.createElement(XmlConfig.Delivery.TAG)
        deliveryBAsElement.setAttribute(XmlConfig.Delivery.ADDRESS, "26464319")
        deliveryBAsElement.setAttribute(XmlConfig.Delivery.DURATION, "600")

        roundRequestAsElement.appendChild(warehouseAsElement)
        roundRequestAsElement.appendChild(deliveryAAsElement)
        roundRequestAsElement.appendChild(deliveryBAsElement)

        val roundRequest: RoundRequest = roundRequestSerializer.unserialize(roundRequestAsElement)

        Assert.assertEquals(25303798, roundRequest.warehouse.address.id)
        Assert.assertTrue(roundRequest.warehouse.departureHour == Instant(8 , 0 , 0))

        Assert.assertEquals(2, roundRequest.deliveries.size)
        Assert.assertEquals(195279, roundRequest.deliveries.first().address.id)

        val delivery = roundRequest.deliveries.first()

        Assert.assertEquals(195279, delivery.address.id)
        Assert.assertEquals(900, delivery.duration.toSeconds())
    }
}