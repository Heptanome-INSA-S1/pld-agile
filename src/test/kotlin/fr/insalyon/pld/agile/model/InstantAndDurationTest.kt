package fr.insalyon.pld.agile.model

import org.junit.Assert.assertEquals
import org.junit.Test

class InstantAndDurationTest {

  @Test
  fun create() {

    val instantA: Instant = Instant(10, 30, 15)
    assertEquals(37815, instantA.toSeconds())

    assertEquals(10, instantA.hour)
    assertEquals(30, instantA.minutes)
    assertEquals(15, instantA.seconds)

    val durationA: Duration = Duration(10, 30, 15)
    assertEquals(37815, durationA.toSeconds())

  }

  @Test
  fun toSeconds() {

    val midnight = Instant()
    val oneHourDuration = Duration(1)
    val oneHourBefore = midnight - oneHourDuration

  }

  @Test
  fun add() {

    val instantA = Instant(minutes = 10)
    val durationA = Duration(minutes = 55)

    val instantB = instantA + durationA

    assertEquals(1, instantB.hour)
    assertEquals(5, instantB.minutes)

  }

  @Test
  fun minus() {

    val instantA = Instant(hour = 3, minutes = 10)
    val durationA = 1.hours + 10.seconds

    val instantB = instantA - durationA

    assertEquals(2, instantB.hour)
    assertEquals(9, instantB.minutes)
    assertEquals(50, instantB.seconds)

  }

  @Test
  fun comparator() {

    val dayDuration = 24.hours
    val future = Instant() + dayDuration
    val past = Instant(1) - dayDuration
    val future2 = Instant(2) + dayDuration
    val past2 = Instant(2) - dayDuration
    val day = Instant(10, 15)

    assert(future > day)
    assert(past < day)
    assert(future > past)
    assert(future == future2)
    assert(past == past2)
    assert(day != future)
    assert(day != past)
    assert(future != past)
    assert(Instant.FUTURE == (day + dayDuration))
    assert(Instant.PAST == (day - dayDuration))

    assert((10 h 45) > (9 h 50))
    assert((10 h 10) + 15.minutes + 20.seconds == (10 h 25 m 20))

  }

  @Test
  fun durationBetweenInstant() {

    assertEquals(10.seconds, (10 h 1 m 50) - (10 h 1 m 40))
    assertEquals(1.minutes + 20.seconds, (0 h 1 m 20) - (0 h 0 m 0))

  }

  @Test
  fun intToDuration() {

    val duration: Duration = 10.hours + 5.minutes + 15.seconds
    assertEquals(36_315, duration.toSeconds())

  }

  @Test
  fun stringToInstant() {

    assertEquals((10 h 45).toSeconds(), "10:45:0".toInstant()!!.toSeconds())
    assertEquals((10 h 45 m 27).toSeconds(), "10:45:27".toInstant()!!.toSeconds())
    assertEquals((0 h 42 m 54).toSeconds(), "0:42:54".toInstant()!!.toSeconds())

  }

}