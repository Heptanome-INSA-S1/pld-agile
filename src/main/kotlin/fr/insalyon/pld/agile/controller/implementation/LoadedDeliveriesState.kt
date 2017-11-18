package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.service.roundcomputing.implementation.RoundComputerImpl

class LoadedDeliveriesState : DefaultState<RoundRequest>(), State<RoundRequest> {

  override fun init(element: RoundRequest, window: Any) {
    println(element)
  }

    override fun calculateRound(controller: Controller) {
        println("Calculate round was called")

        controller.changeStateAndInit(controller.CALCULATED_ROUND_STATE,RoundComputerImpl(plan!!, roundRequest!!).round)
    }
}
