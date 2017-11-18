package fr.insalyon.pld.agile.util.xml.serialization.implementation
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.model.Warehouse
import fr.insalyon.pld.agile.model.XmlConfig
import fr.insalyon.pld.agile.util.xml.serialization.api.XmlSerializer
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList


class RoundRequestSerializer(
        val document: Document,
        private val deliverySerializer: DeliverySerializer,
        private val warehouseSerializer: WarehouseSerializer
) : XmlSerializer<RoundRequest> {

    override fun serialize(element: RoundRequest): Element {
        val result: Element = document.createElement(XmlConfig.RoundRequest.TAG)
        result.appendChild(warehouseSerializer.serialize(element.warehouse))
        element.deliveries.forEach { node -> result.appendChild(deliverySerializer.serialize(node)) }
        return result
    }

    override fun unserialize(element: Element): RoundRequest {

        val warehouse: Warehouse = warehouseSerializer.unserialize(element
                .getElementsByTagName(XmlConfig.Warehouse.TAG)
                .item(0) as Element)
        val deliveries: Set<Delivery> = element
                .getElementsByTagName(XmlConfig.Delivery.TAG)
                .map {
                    val delivery = deliverySerializer.unserialize(it as Element)
                    delivery
                }.toSet()
        return RoundRequest(
            warehouse,
            deliveries
        )
    }

    private fun <T> NodeList.map(transform: (Node) -> T): List<T> = List(length, { index -> transform(item(index)) })


}