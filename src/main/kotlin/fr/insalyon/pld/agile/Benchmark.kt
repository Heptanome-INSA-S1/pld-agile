package fr.insalyon.pld.agile

import java.io.PrintStream

fun <T> benchmark(
    printStream: PrintStream = System.out,
    func: () -> T): T {
  val startTime = System.currentTimeMillis()
  val result = func()
  val endTime = System.currentTimeMillis()
  printStream.println("Duration: ${endTime - startTime} ms")
  return result
}

fun benchmark(
    printStream: PrintStream = System.out,
    func: () -> Unit
) {
  val startTime = System.currentTimeMillis()
  func()
  val endTime = System.currentTimeMillis()
  printStream.println("Duration: ${endTime - startTime} ms")
}