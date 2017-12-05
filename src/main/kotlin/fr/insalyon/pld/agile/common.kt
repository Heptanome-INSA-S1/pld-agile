package fr.insalyon.pld.agile

import java.io.File

fun getResource(path: String): File = File(Config.RESOURCE_FOLDER + path)

fun getTestResource(path: String): File = File(Config.TEST_RESOURCE_FOLDER + path)

fun getResourcePath(path: String): String = Config.RESOURCE_FOLDER + path

fun getTestResourcePath(path: String): String = Config.TEST_RESOURCE_FOLDER + path

val Int.Companion.POSITIVE_INFINITY
  get() = Int.MAX_VALUE - 1

val Int.Companion.NEGATIVE_INFINITY
  get() = Int.MIN_VALUE + 1
