package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.Round
import java.io.File

class CalculatedRoundState : DefaultState<Round>(), State<Round> {

  override fun init(controller: Controller, element: Round) {
    controller.round = element
    println(controller.round)
    controller.window.roundView()
  }

  override fun loadRoundRequest(controller: Controller) {
    defaultLoadRoundRequestImpl(controller)
  }

  override fun loadRoundRequest(controller: Controller, file : File) {
    fileLoadRoundRequestImpl(controller, file)
  }

  override fun calculateRound(controller: Controller) {
    defaultCalculateRoundImpl(controller)
  }
}
