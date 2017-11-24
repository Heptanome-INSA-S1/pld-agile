package fr.insalyon.pld.agile.model

/**
 * An instant of the day : HH:MM:ss
 * If the instant if after the current day it will threaten as "FUTURE"
 * If the instant is before the current day it will be threaten as "PAST"
 */
class Instant private constructor(
    private val _seconds: Int = 0,
    private val alwaysAfter: Boolean = false,
    private val alwaysBefore: Boolean = false
) : Comparable<Instant> {

  companion object {
    private val SECONDS_PER_MINUTES = 60
    private val SECONDS_PER_HOUR = 3600
    private val SECONDS_PER_DAY = 86_000
    val FUTURE = Instant(Int.MAX_VALUE, alwaysAfter = true)
    val PAST = Instant(Int.MIN_VALUE, alwaysBefore = true)
  }

  /**
   * The hour field of the instant
   */
  val hour by lazy { (_seconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR }

  /**
   * The minutes field of the instant
   */
  val minutes by lazy { (_seconds) % SECONDS_PER_HOUR / SECONDS_PER_MINUTES }

  /**
   * The seconds field of the instant
   */
  val seconds by lazy { _seconds % SECONDS_PER_MINUTES }

  constructor(hour: Int = 0, minutes: Int = 0, seconds: Int = 0): this(hour * SECONDS_PER_HOUR + minutes * SECONDS_PER_MINUTES + seconds, false, false)

  override fun compareTo(other: Instant): Int {
    if(alwaysAfter && other.alwaysAfter) return 0
    if(alwaysBefore && other.alwaysBefore) return 0
    if(alwaysAfter) return 1
    if(alwaysBefore) return -1
    if(other.alwaysAfter) return -1
    if(other.alwaysBefore) return 1
    return toSeconds().compareTo(other.toSeconds())
  }

  private fun fromSeconds(seconds: Int): Instant {
    if(seconds < 0) return PAST
    if(seconds >= SECONDS_PER_DAY) return FUTURE
    return Instant(seconds, alwaysAfter, alwaysBefore)
  }

  operator fun plus(duration: Duration): Instant = fromSeconds(toSeconds() + duration.toSeconds())

  operator fun minus(duration: Duration): Instant = fromSeconds(toSeconds() - duration.toSeconds())

  operator fun minus(instant: Instant): Duration = (_seconds - instant._seconds).seconds

  fun toSeconds(): Int = _seconds

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Instant

    if(alwaysAfter && other.alwaysAfter) return true
    if(alwaysBefore && other.alwaysBefore) return true

    if(alwaysAfter || alwaysBefore) return false
    if(other.alwaysAfter || other.alwaysBefore) return false

    if (hour != other.hour) return false
    if (minutes != other.minutes) return false
    if (seconds != other.seconds) return false

    return true
  }

  override fun hashCode(): Int {
    var result = hour.hashCode()
    result = 31 * result + minutes.hashCode()
    result = 31 * result + seconds.hashCode()
    return result
  }

  override fun toString(): String {
    if(alwaysBefore) return "PAST"
    if(alwaysAfter) return "FUTURE"
    return "$hour:$minutes:$seconds"
  }
   fun toFormattedString(): String {
      return if(seconds==0)
        if(minutes==0)
          "$hour h"
        else
          "$hour h $minutes m"
      else
        "$hour h $minutes m $seconds s"
  }


}

infix fun Int.h(minutes: Int): Instant {
  if(this < 0 || this > 23) throw IllegalStateException("Hour must be between 0 and 24")
  if(minutes < 0 || minutes > 59) throw IllegalStateException("Minutes must be between 0 and 59")
  return Instant(this, minutes)
}

infix fun Instant.m(seconds: Int): Instant {
  if(seconds < 0 || seconds > 59) throw IllegalStateException("Seconds must be between 0 and 60")
  return this + Duration(seconds = seconds)
}

fun String.toInstant(delimiter: String = ":"): Instant {
  val splittedString = split(delimiter)
  val hourOfDay: Int = splittedString[0].toInt()
  val minutes: Int = splittedString[1].toInt()
  val seconds: Int = splittedString[2].toInt()
  return Instant(hourOfDay, minutes, seconds)
}