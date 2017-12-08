package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable

/**
 * A duration between two events
 */
class Duration private constructor(
    private val _seconds: Int = 0
) : Comparable<Measurable>, Measurable {

  override val length: Int
    get() = _seconds

  constructor(hour: Int = 0, minutes: Int = 0, seconds: Int = 0): this(hour * 3600 + minutes * 60 + seconds)

  operator fun plus(other: Duration): Duration = Duration(_seconds + other.toSeconds())

  operator fun minus(other: Duration): Duration {
    return Duration(_seconds - other._seconds)
  }

  val hours: Int
  get() {
    return _seconds / 3600 % 24
  }
  val minutes: Int
  get() {
    return _seconds / 60 % 60
  }
  val secondes: Int
  get() {
    return _seconds % 60
  }

  fun toSeconds() = _seconds

  fun toMillis() = _seconds * 1000L

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Duration

    if (_seconds != other._seconds) return false

    return true
  }

  override fun hashCode(): Int = _seconds.hashCode()

  override fun toString():String{
    var res = ""
    if (hours != 0)
      res += hours.toString() + "h"

    if (minutes != 0)
      res += minutes.toString() + "m"

    res += secondes.toString() + "s"
    return res
  }


  fun toShortFormattedString(): String {
    var res = ""

    if (hours > 0)
      res += "${hours}h"
    if (minutes > 9)
      res += "${minutes}m"
    else
      res += "0${minutes}m"

    return res
  }

}

val Int.hours: Duration get() = Duration(hour = this)
val Int.minutes: Duration get() = Duration(minutes = this)
val Int.seconds: Duration get() = Duration(seconds = this)
val Long.hours: Duration get() = Duration(hour = this.toInt())
val Long.minutes: Duration get() = Duration(minutes = this.toInt())
val Long.seconds: Duration get() = Duration(seconds = this.toInt())
