package fr.insalyon.pld.agile.service.roundmodifier.implementaion

import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.roundcomputing.api.RoundComputer
import fr.insalyon.pld.agile.service.roundcomputing.implementation.RoundComputerImpl
import fr.insalyon.pld.agile.service.roundmodifier.implementation.RoundModifierImp
import org.junit.Assert
import org.junit.Test

class RoundModifierImpTest {

    val source = Intersection(2,0,0)

    val node1 = Intersection(1,0,0)
    val node3 = Intersection(3,0,0)
    val node4 = Intersection(4,0,0)
    val node5 = Intersection(5,0,0)
    val node6 = Intersection(6,0,0)
    val node7 = Intersection(7,0,0)

    val plan by lazy {

        val roadOfLength1 = Junction(1, "")
        val roadOfLength2 = Junction(2, "")
        val roadOfLength4 = Junction(4, "")

        Plan(
                setOf<Intersection>(node1, source, node3, node4, node5, node6, node7),
                setOf(
                        //Triple(srcNode, road, destNode)
                        Triple(node1, roadOfLength1, source),
                        Triple(node1, roadOfLength4, node3),
                        Triple(source, roadOfLength1, node4),
                        Triple(source, roadOfLength2, node5),
                        Triple(node4, roadOfLength1, node3),
                        Triple(node4, roadOfLength2, node5),
                        Triple(node5, roadOfLength1, node6),
                        Triple(node6, roadOfLength1, source),
                        Triple(node4,roadOfLength1,node7),
                        Triple(node7, roadOfLength1, node6)

                )
        )
    }

    val roundRequest = RoundRequest(
            Warehouse(address = source, departureHour = 8 h 0 m 0),
            setOf(
                    Delivery(node4, duration = 200.seconds),
                    Delivery(node5, duration = 400.seconds),
                    Delivery(node6, duration = 100.seconds)
            )
    )

    @Test
    fun removeDeliveryAtFirstPosition(){
        val roundComputer: RoundComputer = RoundComputerImpl(plan, roundRequest, 15.km_h)
        val round = roundComputer.round

        val roundModifier = RoundModifierImp(roundRequest.deliveries.elementAt(0),round,plan)
        roundModifier.removeDelivery(Delivery(node4, duration = 200.seconds),round)

        Assert.assertEquals(3, round.path().size)

        Assert.assertEquals(2,round.path().elementAt(0).edges[0].length)
        Assert.assertEquals(2,round.path().elementAt(0).nodes[0].id)
        Assert.assertEquals(5,round.path().elementAt(0).nodes[1].id)

        Assert.assertEquals(1, round.path().elementAt(1).edges[0].length)
        Assert.assertEquals(5,  round.path().elementAt(1).nodes[0].id)
        Assert.assertEquals(6,round.path().elementAt(1).nodes[1].id)

        Assert.assertEquals(1, round.path().elementAt(2).edges[0].length)
        Assert.assertEquals(6,round.path().elementAt(2).nodes[0].id)
        Assert.assertEquals(2,round.path().elementAt(2).nodes[1].id)
    }

    @Test
    fun removeDeliveryTestNotAtFirstPosition(){
        val roundComputer: RoundComputer = RoundComputerImpl(plan, roundRequest, 15.km_h)
        val round = roundComputer.round

        val roundModifier = RoundModifierImp(roundRequest.deliveries.elementAt(1),round,plan)
        roundModifier.removeDelivery(Delivery(node5, duration = 400.seconds),round)

        Assert.assertEquals(2,round.deliveries().size)
        Assert.assertEquals(4,round.deliveries().elementAt(0).address.id)
        Assert.assertEquals(6,round.deliveries().elementAt(1).address.id)

        Assert.assertEquals(1,round.path().elementAt(0).edges[0].length)
        Assert.assertEquals(2,round.path().elementAt(0).nodes[0].id)
        Assert.assertEquals(4,round.path().elementAt(0).nodes[1].id)

        Assert.assertEquals(1, round.path().elementAt(1).edges[0].length)
        Assert.assertEquals(1, round.path().elementAt(1).edges[1].length)
        Assert.assertEquals(4, round.path().elementAt(1).nodes[0].id)
        Assert.assertEquals(7, round.path().elementAt(1).nodes[1].id)
        Assert.assertEquals(6, round.path().elementAt(1).nodes[2].id)

        Assert.assertEquals(1,round.path().elementAt(2).edges[0].length)
        Assert.assertEquals(6,round.path().elementAt(2).nodes[0].id)
        Assert.assertEquals(2,round.path().elementAt(2).nodes[1].id)
    }

    @Test
    fun removeDeliveryTestLastPosition() {
        val roundComputer: RoundComputer = RoundComputerImpl(plan, roundRequest, 15.km_h)
        val round = roundComputer.round

        val roundModifier = RoundModifierImp(roundRequest.deliveries.elementAt(2),round,plan)
        roundModifier.removeDelivery(Delivery(node6, duration = 100.seconds),round)

        Assert.assertEquals(2,round.deliveries().size)
        Assert.assertEquals(4,round.deliveries().elementAt(0).address.id)
        Assert.assertEquals(5,round.deliveries().elementAt(1).address.id)

        Assert.assertEquals(4, round.path().elementAt(0).nodes[1].id)

        Assert.assertEquals(5, round.path().elementAt(1).nodes[1].id)


        Assert.assertEquals(6, round.path().elementAt(2).nodes[1].id)
        Assert.assertEquals(2,round.path().elementAt(2).nodes[2].id)
    }
}