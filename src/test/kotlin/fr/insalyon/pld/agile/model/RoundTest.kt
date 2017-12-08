package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable
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

    val round = Round(warehouse, linkedSetOf(deliveryA, deliveryB), listOf(
        pathFromWareHouseToDeliveryA,
        pathFromDeliveryAToDeliveryB,
        pathFromDeliveryBToWarehouse
    ), listOf(
        pathFromWareHouseToDeliveryA,
        pathFromDeliveryAToDeliveryB,
        pathFromDeliveryBToWarehouse)
    )

    assertEquals(9 h 45, warehouse.departureHour + round.length.seconds)

  }

  @Test
  fun addDelivery() {

    val int1 = Intersection(1)
    val int2 = Intersection(2)
    val int3 = Intersection(3)
    val int4 = Intersection(4)

    val intersections = setOf(int1, int2, int3)

    val warehouse = Warehouse(int1, 8 h 5)
    val deliveryA = Delivery(int2, startTime = 8 h 30, duration = 10.minutes)
    val deliveryB = Delivery(int3, duration = 20.minutes)

    val pathFromWareHouseToDeliveryA = Path(listOf(int1, int2), listOf(Junction(15.minutes.toSeconds(), "Road Warehouse to A")))
    val pathFromDeliveryAToDeliveryB = Path(listOf(int2, int3), listOf(Junction(30.minutes.toSeconds(), "Road from A to B")))
    val pathFromDeliveryBToWarehouse = Path(listOf(int3, int1), listOf(Junction(15.minutes.toSeconds(), "Road from B to Warehouse")))

    val round = Round(warehouse, linkedSetOf(deliveryA, deliveryB), listOf(
        pathFromWareHouseToDeliveryA,
        pathFromDeliveryAToDeliveryB,
        pathFromDeliveryBToWarehouse
    ), listOf(
        pathFromWareHouseToDeliveryA,
        pathFromDeliveryAToDeliveryB,
        pathFromDeliveryBToWarehouse
    ))


    round.addDelivery(SubPath(
        Path(listOf(int1, int4), listOf(Junction(20.minutes.toSeconds(), "Road Warehouse to New"))),
        Path(listOf(int1, int4), listOf(Junction(20.minutes.toSeconds(), "Road Warehouse to New"))).toDuration(1.m_s),
        Delivery(int4, duration = 15.minutes),
        Path(listOf(int4, int2), listOf(Junction(10.minutes.toSeconds(), "Road from New to DeliveryA"))),
        Path(listOf(int4, int2), listOf(Junction(10.minutes.toSeconds(), "Road from New to DeliveryA"))).toDuration(1.m_s)
    ))

    assertEquals(3, round.deliveries().size)
    assertEquals("1 -> 4 -> 2 -> 3 -> 1", round.toString())

  }

  @Test
  fun removeDelivery() {
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

    val round = Round(warehouse, linkedSetOf(deliveryA, deliveryB), listOf(
        pathFromWareHouseToDeliveryA,
        pathFromDeliveryAToDeliveryB,
        pathFromDeliveryBToWarehouse
    ), listOf(
        pathFromWareHouseToDeliveryA,
        pathFromDeliveryAToDeliveryB,
        pathFromDeliveryBToWarehouse
    ))

    round.removeDelivery(deliveryA, Path(listOf(int1, int3), listOf(Junction(10.seconds.toSeconds(), "ShortRoad between Warehouse and B"))), 10.seconds)

    assertEquals(1, round.deliveries().size)
    assertEquals("1 -> 3 -> 1", round.toString())

  }

  @Test
  fun lengthUpdate() {

    val round = Round(
        Warehouse(Intersection(1), 8 h 5),
        linkedSetOf(
            Delivery(Intersection(2), duration = 900.seconds),
            Delivery(Intersection(3), duration = 900.seconds)
        ), listOf(
        object : Measurable {
          override val length = 500
        },
        object : Measurable {
          override val length = 300
        },
        object : Measurable {
          override val length = 400
        }
    ), listOf(
        Path(listOf(), listOf()),
        Path(listOf(), listOf()),
        Path(listOf(), listOf())
    )
    )

    assertEquals(500 + 900 + 300 + 900 + 400, round.length)

    val replacePath = Path(
        listOf(Intersection(2), Intersection(1)),
        listOf(Junction(250, "A"))
    )
    round.removeDelivery(round.deliveries().elementAt(1), replacePath, 250.seconds)

    assertEquals(500 + 900 + 250, round.length)

  }

  @Test
  fun testWaitingTimeDuration() {
    val round = Round(
        Warehouse(Intersection(1), 8 h 0),
        linkedSetOf(
            Delivery(Intersection(2), startTime = 8 h 20, duration = 5.minutes),
            Delivery(Intersection(3), startTime = 8 h 40, duration = 15.minutes)
        ), listOf(10.minutes, 10.minutes, 20.minutes), listOf(
        Path(listOf(), listOf()),
        Path(listOf(), listOf()),
        Path(listOf(), listOf())
    )
    )
    assertEquals(listOf(10.minutes, 5.minutes), round.getWaitingTimes())
  }

  @Test
  fun testEarliestArrivalTime() {

    /*    15m     20m      10m
       W ---> D1 ----> D2 -----> W
     8:0    [10m]     [30m]
     */

    val round = Round(
        Warehouse(Intersection(1L), 8 h 0),
        linkedSetOf(
            Delivery(Intersection(2L), null, null, 10.minutes),
            Delivery(Intersection(3L), null, null, 30.minutes)
        ), listOf(15.minutes, 20.minutes, 10.minutes), listOf()
    )

    assertEquals(listOf(8 h 15, 8 h 45), round.getEarliestArrivalTimes())

  }

  @Test
  fun testEarliestDepartureTime() {

    /*    15m     20m      10m
       W ---> D1 ----> D2 -----> W
     8:0    [10m]     [30m]
     */

    val round = Round(
        Warehouse(Intersection(1L), 8 h 0),
        linkedSetOf(
            Delivery(Intersection(2L), null, null, 10.minutes),
            Delivery(Intersection(3L), null, null, 30.minutes)
        ), listOf(15.minutes, 20.minutes, 10.minutes), listOf()
    )

    assertEquals(listOf(8 h 25, 9 h 15, 18 h 0), round.getEarliestDepartureTime())

  }

  @Test
  fun testLatestDepartureTime() {


    /*    15m     20m      10m
       W ---> D1 ----> D2 -----> W
     8:0    [10m]     [30m]    <=18:0
     */

    val round = Round(
        Warehouse(Intersection(1L), 8 h 0),
        linkedSetOf(
            Delivery(Intersection(2L), null, null, 10.minutes),
            Delivery(Intersection(3L), null, null, 30.minutes)
        ), listOf(15.minutes, 20.minutes, 10.minutes), listOf()
    )

    assertEquals(listOf(17 h 0, 17 h 50), round.getLastestDepartureTime())

  }

  @Test
  fun testLatestArrivalTime() {


    /*    15m     20m      10m
       W ---> D1 ----> D2 -----> W
     8:0    [10m]     [30m]    <=18:0
     */

    val round = Round(
        Warehouse(Intersection(1L), 8 h 0),
        linkedSetOf(
            Delivery(Intersection(2L), null, null, 10.minutes),
            Delivery(Intersection(3L), null, null, 30.minutes)
        ), listOf(15.minutes, 20.minutes, 10.minutes), listOf()
    )

    assertEquals(listOf(16 h 50, 17 h 20, 18 h 0), round.getLatestArrivalTime())

  }

}