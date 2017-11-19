package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.Round

class CalculatedRoundState : DefaultState<Round>(), State<Round> {

  override fun init(controller: Controller, element: Round) {
    println("Round was well calculated")
    controller.round = element
    println(controller.round)
  }

  override fun loadRoundRequest(controller: Controller) {
    defaultLoadRoundRequestImpl(controller)
  }

  override fun calculateRound(controller: Controller) {
    defaultCalculateRoundImpl(controller)
  }
}
