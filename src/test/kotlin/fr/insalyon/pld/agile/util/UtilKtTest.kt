package fr.insalyon.pld.agile.util

import fr.insalyon.pld.agile.model.minutes
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilKtTest {

  @Test
  fun testMax() {

    assertEquals(5, max(0, 5))
    assertEquals(5.minutes, max(2.minutes, 5.minutes))
    assertEquals(10.minutes, max(10.minutes, 5.minutes))
    assertEquals(10.minutes, max(10.minutes, 10.minutes))
    assertEquals(15.minutes, max(null, 15.minutes))
    assertEquals(9.minutes, max(9.minutes, null))

  }

  @Test
  fun testMin() {
    assertEquals(0, min(0, 5))
    assertEquals(2.minutes, min(2.minutes, 5.minutes))
    assertEquals(5.minutes, min(10.minutes, 5.minutes))
    assertEquals(10.minutes, min(10.minutes, 10.minutes))
    assertEquals(15.minutes, min(null, 15.minutes))
    assertEquals(9.minutes, min(9.minutes, null))
  }

}