package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Path
import org.junit.Assert.assertEquals
import org.junit.Test

class RoundTest {

  @Test
  fun length() {

    val int1 = Intersection(1)
    val int2 = Intersection(2)
    val int3 = Intersection(3)

    val intersections = setOf(int1, int2, int3)

    val warehouse = Warehouse(int1, 8 h 5)
    val deliveryA = Delivery(int2, startTime = 8 h 30, duration = 10.minutes)
    val deliveryB = Delivery(int3, duration = 20.minutes)

    val pathFromWareHouseToDeliveryA = Path(listOf(int1, int2), listOf(Junction(15.minutes.toSeconds(), "Road Warehouse to A")))
    val pathFromDeliveryAToDeliveryB = Path(listOf(int2, int3), listOf(Junction(30.minutes.toSeconds(), "Road from A to B")))
    val pathFromDeliveryBToWarehouse = Path(listOf(int3, int1), listOf(Junction(15.minutes.toSeconds(), "Road from B to Warehouse")))

    val round = Round(warehouse, linkedSetOf(deliveryA, deliveryB), linkedSetOf(
        pathFromWareHouseToDeliveryA,
        pathFromDeliveryAToDeliveryB,
        pathFromDeliveryBToWarehouse
    ))

    assertEquals(9 h 45, warehouse.departureHour + round.length.seconds)

  }

}