package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.util.Logger
import java.io.File

class LoadedDeliveriesState : DefaultState<RoundRequest>() {

  override fun init(controller: Controller, element: RoundRequest) {
    controller.roundRequest = element
  }

  override fun loadRoundRequest(controller: Controller) {
    defaultLoadRoundRequestImpl(controller)
  }

  override fun loadRoundRequest(controller: Controller, file : File) {
    fileLoadRoundRequestImpl(controller, file)
  }

  override fun calculateRound(controller: Controller) {
    Logger.info("Calculate round was called")
    try {
      defaultCalculateRoundImpl(controller)
    } catch (e: Exception) {
      controller.changeStateAndInit(controller.LOADED_PLAN_STATE, controller.plan!!)
      controller.manageException(e)
    }
  }
}
