package fr.insalyon.pld.agile.util

import fr.insalyon.pld.agile.Config.Util.LOGGER_LEVEL

class Logger {

  enum class Level(val level: Int, val color: Int) {
    DEBUG(0, Logger.blue),
    INFO(1, Logger.green),
    WARN(2, Logger.yellow),
    ERROR(3, Logger.red),
    FATAL(4, Logger.red)
  }

  operator fun Level.compareTo(other: Level): Int {
    return level.compareTo(other.level)
  }

  companion object {

    private val red = 31
    private val blue = 34
    private val green = 32
    private val yellow = 33
    private val default = 39

    val DEBUG = Level.DEBUG
    val INFO = Level.INFO
    val WARN = Level.WARN
    val ERROR = Level.ERROR
    val FATAL = Level.FATAL

    private fun setColor(color: Int) {
      print(27.toChar() + "[${color}m")
    }

    private fun resetColor() {
      print(27.toChar() + "[${default}m")
    }

    fun debug(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.DEBUG) {
        setColor(Level.DEBUG.color)
        args.forEach { println(it) }
        resetColor()
      }
    }

    fun info(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.INFO) {
        setColor(Level.INFO.color)
        args.forEach { println(it) }
        resetColor()
      }
    }

    fun warn(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.WARN) {
        setColor(Level.WARN.color)
        args.forEach { println(it) }
        resetColor()
      }
    }

    fun error(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.ERROR) {
        setColor(Level.ERROR.color)
        args.forEach { println(it) }
        resetColor()
      }
    }

    fun fatal(vararg args: Any) {
      if (LOGGER_LEVEL <= Level.FATAL) {
        setColor(Level.FATAL.color)
        args.forEach { println(it) }
        resetColor()
      }
    }

  }

}