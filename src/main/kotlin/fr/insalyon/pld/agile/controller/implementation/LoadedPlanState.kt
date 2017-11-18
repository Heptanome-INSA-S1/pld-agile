package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.model.Plan

class LoadedPlanState : DefaultState<Plan>(){

  override fun init(controller: Controller, element: Plan) {
    println("The plan has been well loaded")
    controller.plan = element
  }

  override fun loadRoundRequest(controller: Controller) {
    defaultLoadRoundRequestImpl(controller)
  }
}