package fr.insalyon.pld.agile.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SpeedTest {

  @Test
  fun creator() {

    val speed = 2.km_h

    assertEquals(2, speed.value)
    assertEquals(Speed.DistanceUnit.KM, speed.distanceUnit)
    assertEquals(Speed.DurationUnit.H, speed.durationUnit)

  }

  @Test
  fun convertissor() {

    val speed = 15.km_h
    assertEquals(15 * 1000, speed.to(Speed.DistanceUnit.M, Speed.DurationUnit.H).value)
    assertEquals(15 * 60, speed.to(Speed.DistanceUnit.KM, Speed.DurationUnit.M).value)
    assertEquals(15 * 1000 * 3600, speed.to(Speed.DistanceUnit.M, Speed.DurationUnit.S).value)

  }

}