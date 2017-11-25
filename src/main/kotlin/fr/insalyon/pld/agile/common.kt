package fr.insalyon.pld.agile

import java.io.File

fun getResource(path: String): File = File(Config.RESOURCE_FOLDER + path)

fun getTestResource(path: String): File = File(Config.TEST_RESOURCE_FOLDER + path)

fun getResourcePath(path: String): String = Config.RESOURCE_FOLDER + path

fun getTestResourcePath(path: String): String = Config.TEST_RESOURCE_FOLDER + path

val Long.Companion.POSITIVE_INFINITY
  get() = Long.MAX_VALUE - 1L

val Long.Companion.NEGATIVE_INFINITY
  get() = Long.MIN_VALUE + 1L

fun <E> Iterable<E>.sumLongBy(selector: (E) -> Long): Long {
  var sum = 0L
  this.forEach{ sum += selector(it)}
  return sum
}