package fr.insalyon.pld.agile.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DistanceTest {

    @Test
    fun convertissor()
    {
        val d0  =  1.dam
        assertEquals(10, d0.to(Distance.DistanceUnit.M).value)
        assertEquals(1,d0.to(Distance.DistanceUnit.DAM).value)
        assertEquals(0, d0.to(Distance.DistanceUnit.KM).value)

        val d1 = 10.km
        assertEquals(10000, d1.to(Distance.DistanceUnit.M).value)
        assertEquals(1000,d1.to(Distance.DistanceUnit.DAM).value)
        assertEquals(10, d1.to(Distance.DistanceUnit.KM).value)

        val d2 = 1.m
        assertEquals(1, d2.to(Distance.DistanceUnit.M).value)
        assertEquals(0, d2.to(Distance.DistanceUnit.DAM).value)
        assertEquals(0, d2.to(Distance.DistanceUnit.KM).value)
    }
}