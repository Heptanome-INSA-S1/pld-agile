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
    if(other._seconds > this._seconds) {
      throw IllegalStateException("Cannot compute negative duration")
    }
    return Duration(_seconds - other._seconds)
  }

  fun toSeconds() = _seconds
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Duration

    if (_seconds != other._seconds) return false

    return true
  }

  override fun hashCode(): Int = _seconds.hashCode()

}

val Int.hours: Duration get() = Duration(hour = this)
val Int.minutes: Duration get() = Duration(minutes = this)
val Int.seconds: Duration get() = Duration(seconds = this)