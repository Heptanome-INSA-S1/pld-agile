package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.util.Logger
import java.io.File

class ErrorState : DefaultState<Pair<Exception, State<Nothing>>>(){

  var previousState: State<Nothing> = this

  override fun init(controller: Controller, element: Pair<Exception, State<Nothing>>) {
    val exception = element.first
    previousState = element.second
    exception.printStackTrace()
    controller.window.errorPopUp(exception.localizedMessage)
  }

  override fun loadPlan(controller: Controller) {
    Logger.info("loadPlan was call in ErrorState")
  }

  override fun loadPlan(controller: Controller, file: File) {
    Logger.info("loadPlan with file was call in ErrorState")
  }

  override fun loadRoundRequest(controller: Controller) {
    Logger.info("loadRoundRequest was call in ErrorState")
  }

  override fun loadRoundRequest(controller: Controller, file : File) {
    Logger.info("loadRoundRequest with file was call in ErrorState")
  }

  override fun ok(controller: Controller) {
    Logger.info("Ok was call from ERROR_STATE")
    controller.changeState(previousState)
  }

}