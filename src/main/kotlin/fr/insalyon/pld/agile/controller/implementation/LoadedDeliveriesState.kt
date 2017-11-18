package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.model.RoundRequest

class LoadedDeliveriesState : DefaultState<RoundRequest>() {

  override fun init(controller: Controller, element: RoundRequest) {
    println("Round request was well loaded")
    controller.roundRequest = element
  }

  override fun loadRoundRequest(controller: Controller) {
    defaultLoadRoundRequestImpl(controller)
  }

  override fun calculateRound(controller: Controller) {
    println("Calculate round was called")
    defaultCalculateRoundImpl(controller)
  }
}
