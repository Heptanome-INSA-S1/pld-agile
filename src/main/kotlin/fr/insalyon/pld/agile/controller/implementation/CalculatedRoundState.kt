package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.seconds
import fr.insalyon.pld.agile.util.Logger
import java.io.File

class CalculatedRoundState : DefaultState<Round>(), State<Round> {

  override fun init(controller: Controller, element: Round) {
    controller.round = element
    Logger.info(controller.round!!.warehouse.departureHour + controller.round!!.length.seconds)
    controller.window.refreshRound()
  }

  override fun loadRoundRequest(controller: Controller) {
    defaultLoadRoundRequestImpl(controller)
  }

  override fun loadRoundRequest(controller: Controller, file : File) {
    fileLoadRoundRequestImpl(controller, file)
  }

  override fun calculateRound(controller: Controller) {
    try {
      defaultCalculateRoundImpl(controller)
    } catch (e: Exception) {
      controller.manageException(e)
    }

  }
}
