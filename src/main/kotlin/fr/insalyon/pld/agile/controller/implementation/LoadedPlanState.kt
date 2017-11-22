package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.model.Plan
import java.io.File

class LoadedPlanState : DefaultState<Plan>(){

  override fun init(controller: Controller, element: Plan) {
    println("The plan has been well loaded")
    controller.plan = element
    controller.window.planView()
  }

  override fun loadRoundRequest(controller: Controller) {
    defaultLoadRoundRequestImpl(controller)
  }

  override fun loadRoundRequest(controller: Controller, file : File) {
    fileLoadRoundRequestImpl(controller, file)
  }
}