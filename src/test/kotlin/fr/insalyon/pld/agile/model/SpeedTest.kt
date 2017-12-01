package fr.insalyon.pld.agile.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SpeedTest {

  @Test
  fun creator() {

    val speed = 2.km_h

    assertEquals(2.0, speed.value, .0)
    assertEquals(Speed.DistanceUnit.KM, speed.distanceUnit)
    assertEquals(Speed.DurationUnit.H, speed.durationUnit)

  }

  @Test
  fun convertissor() {

    val speed = 15.km_h
    assertEquals(15.0 * 1000.0, speed.to(Speed.DistanceUnit.M, Speed.DurationUnit.H).value, .1)
    assertEquals(15.0 / 60.0, speed.to(Speed.DistanceUnit.KM, Speed.DurationUnit.M).value, .1)
    assertEquals(15.0 * 1000.0 / 3600.0, speed.to(Speed.DistanceUnit.M, Speed.DurationUnit.S).value, .1)

  }

}