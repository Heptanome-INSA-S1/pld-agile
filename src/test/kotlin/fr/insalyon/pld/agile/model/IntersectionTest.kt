package fr.insalyon.pld.agile.model

import org.junit.Assert
import org.junit.Test

class IntersectionTest {

    val node1 = Intersection(1,0,0)
    val node2 = Intersection(3,0,0)
    val node3 = Intersection(3,0,0)

    val del1 = Delivery(node1, duration = 200.seconds)
    val del2 = Delivery(node1, duration = 400.seconds)
    val del3 = Delivery(node2, duration = 400.seconds)

    @Test
    fun equal(){
        Assert.assertEquals(true, node2.equals(node3))
        Assert.assertEquals(false, node1.equals(node2))

        Assert.assertEquals(true, del1.address == del2.address)
        Assert.assertEquals(false, del2.address == del3.address)
    }
}