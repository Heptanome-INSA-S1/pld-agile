package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.Plan

class LoadedPlanState : DefaultState<Plan>(), State<Plan> {

  override fun init(element: Plan) {
    println(element)
  }
}