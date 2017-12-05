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
    var secondes = _seconds
    if(secondes==0)
      return "0s"
    val hours : Int = secondes / 3600
    secondes = secondes - hours * 3600
    val minutes : Int = secondes / 60
    secondes = secondes - minutes * 60
    var res = ""
    if(hours != 0)
      res += ""+ hours + "h"
    if(minutes != 0)
      res += ""+ minutes + "m"
    if(secondes != 0)
      res += ""+ secondes + "s"
    return res
  }

}

val Int.hours: Duration get() = Duration(hour = this)
val Int.minutes: Duration get() = Duration(minutes = this)
val Int.seconds: Duration get() = Duration(seconds = this)
val Long.hours: Duration get() = Duration(hour = this.toInt())
val Long.minutes: Duration get() = Duration(minutes = this.toInt())
val Long.seconds: Duration get() = Duration(seconds = this.toInt())