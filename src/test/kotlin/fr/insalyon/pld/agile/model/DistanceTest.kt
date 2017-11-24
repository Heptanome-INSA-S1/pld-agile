package fr.insalyon.pld.agile.model

import org.junit.Assert
import org.junit.Test

class DistanceTest {

    @Test
    fun convertissor()
    {
        val d0  =  1.dam
        Assert.assertEquals(10, d0.to(Distance.DistanceUnit.M).value)
        Assert.assertEquals(1,d0.to(Distance.DistanceUnit.DAM).value)
        Assert.assertEquals(0.0.toInt(), d0.to(Distance.DistanceUnit.KM).value)

        val d1 = 10.km
        Assert.assertEquals(10000, d1.to(Distance.DistanceUnit.M).value)
        Assert.assertEquals(1000,d1.to(Distance.DistanceUnit.DAM).value)
        Assert.assertEquals(10, d1.to(Distance.DistanceUnit.KM).value)

        val d2 = 1.m
        Assert.assertEquals(1, d2.to(Distance.DistanceUnit.M).value)
        Assert.assertEquals(0, d2.to(Distance.DistanceUnit.DAM).value)
        Assert.assertEquals(0, d2.to(Distance.DistanceUnit.KM).value)
    }
}