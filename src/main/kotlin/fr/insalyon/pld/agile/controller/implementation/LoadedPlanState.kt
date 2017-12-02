package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.util.Logger
import java.io.File

class LoadedPlanState : DefaultState<Plan>(){

  override fun init(controller: Controller, element: Plan) {
    Logger.info("Etat actuel = LOADED_PLAN_STATE")
    Logger.info("The plan has been well loaded")
    controller.plan = element
    controller.window.refreshAll()
  }

  override fun loadRoundRequest(controller: Controller) {
    defaultLoadRoundRequestImpl(controller)
  }

  override fun loadRoundRequest(controller: Controller, file : File) {
    fileLoadRoundRequestImpl(controller, file)
  }

}