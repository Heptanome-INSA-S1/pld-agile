package fr.insalyon.pld.agile.util

import fr.insalyon.pld.agile.Config.LOGGER_LEVEL

class Logger {

  enum class Level(val level: Int) {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3),
    FATAL(4)
  }

  operator fun Level.compareTo(other: Level): Int {
    return level.compareTo(other.level)
  }

  companion object {

    val DEBUG = Level.DEBUG
    val INFO = Level.INFO
    val WARN = Level.WARN
    val ERROR = Level.ERROR
    val FATAL = Level.FATAL

    fun debug(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.DEBUG) {
        args.forEach { println(it) }
      }
    }

    fun info(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.INFO) {
        args.forEach { println(it) }
      }
    }

    fun warn(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.WARN) {
        args.forEach { println(it) }
      }
    }

    fun error(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.ERROR) {
        args.forEach { println(it) }
      }
    }

    fun fatal(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.DEBUG) {
        args.forEach { println(it) }
      }
    }

  }

}