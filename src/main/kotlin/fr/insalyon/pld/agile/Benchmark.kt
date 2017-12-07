package fr.insalyon.pld.agile

import java.io.PrintStream

@JvmName("benchmarkWithReturn")
fun <T> benchmark(
    printStream: PrintStream? = null,
    func: () -> T): Pair<Long, T> {
  val startTime = System.currentTimeMillis()
  val result = func()
  val endTime = System.currentTimeMillis()
  printStream?.println("Duration: ${endTime - startTime} ms")
  return Pair(endTime - startTime, result)
}

@JvmName("benchmarkUnit")
fun benchmark(
    printStream: PrintStream? = null,
    func: () -> Unit
): Pair<Long, Unit> {
  val startTime = System.currentTimeMillis()
  func()
  val endTime = System.currentTimeMillis()
  printStream?.println("Duration: ${endTime - startTime} ms")
  return Pair(endTime - startTime, Unit)
}