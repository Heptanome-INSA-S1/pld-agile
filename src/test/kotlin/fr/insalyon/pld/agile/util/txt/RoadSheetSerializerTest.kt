package fr.insalyon.pld.agile.util.txt

import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.roundcomputing.implementation.RoundComputerImpl
import org.junit.Test

class RoadSheetSerializerTest(){

    @Test
    fun serialize(){
        val plan = Plan(
                setOf(Intersection(25303798, 4, 20),
                        Intersection(195279, 10, 15),
                        Intersection(12345, 10, 17 ),
                        Intersection(26464319, 10, 20),
                        Intersection(345678, 8, 75)
                ),
                setOf<Triple<Intersection, Junction, Intersection>>(
                        Triple(
                                Intersection(25303798, 4, 20),Junction(100, "J1"),Intersection(195279, 10, 15)
                        ),
                        Triple(
                                Intersection(195279, 10, 15),Junction(100, "J2"), Intersection(12345, 10, 17 )
                        ),
                        Triple(
                                Intersection(12345, 10, 17 ), Junction(100, "J3"), Intersection(26464319, 10, 20)
                        ),
                        Triple(
                                Intersection(26464319, 10, 20), Junction(100, "J4"), Intersection(345678, 8, 75)
                        ),
                        Triple(Intersection(345678, 8, 75), Junction(100, "J5"), Intersection(25303798, 4, 20))
                )
        )

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

        val round: Round = RoundComputerImpl(plan = plan, roundRequest = roundRequest, speed = 15.km_h).round
        assert(round.deliveries().size==2)

        RoadSheetSerializer().serialize(round)
    }
}