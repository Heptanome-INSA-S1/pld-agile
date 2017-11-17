package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.RoundRequest

class LoadedDeliveriesState : DefaultState<RoundRequest>(), State<RoundRequest> {

  override fun init(element: RoundRequest, window: Any) {
    println(element)
  }
}
