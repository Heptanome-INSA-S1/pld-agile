package fr.insalyon.pld.agile.model

import fr.insalyon.pld.agile.lib.graph.model.Measurable

/**
 * A duration between two events
 */
class Duration private constructor(
    private val _seconds: Long = 0L
) : Comparable<Measurable>, Measurable {

  override val length: Long
    get() = _seconds

  constructor(hour: Int = 0, minutes: Int = 0, seconds: Int = 0): this(hour * 3600L + minutes * 60L + seconds)

  operator fun plus(other: Duration): Duration = Duration(_seconds + other.toSeconds())

  operator fun minus(other: Duration): Duration {
    if(other._seconds > this._seconds) {
      throw IllegalStateException("Cannot compute negative duration")
    }
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
    if(secondes==0L)
      return "0s"
    val hours : Long = secondes / 3600L
    secondes = secondes - hours * 3600L
    val minutes : Long = secondes / 60L
    secondes = secondes - minutes * 60L
    var res = ""
    if(hours != 0L)
      res += ""+ hours + "h"
    if(minutes != 0L)
      res += ""+ minutes + "m"
    if(secondes != 0L)
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