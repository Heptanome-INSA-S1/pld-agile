package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State
import java.io.File

class ErrorState : DefaultState<Pair<Exception, State<Nothing>>>(){

  var previousState: State<Nothing> = this

  override fun init(controller: Controller, element: Pair<Exception, State<Nothing>>) {
    val exception = element.first
    previousState = element.second
   // controller.window.counter.value = "Error: ${exception.message}"
  }

  override fun loadPlan(controller: Controller) {
    println("loadPlan was call in ErrorState")
  }

  override fun loadPlan(controller: Controller, file: File) {
    println("loadPlan with file was call in ErrorState")
  }

  override fun loadRoundRequest(controller: Controller) {
    println("loadRoundRequest was call in ErrorState")
  }

  override fun loadRoundRequest(controller: Controller, file : File) {
    println("loadRoundRequest with file was call in ErrorState")
  }

  override fun ok(controller: Controller) {
   // controller.window.counter.value = ""
    controller.changeState(previousState)
  }

}