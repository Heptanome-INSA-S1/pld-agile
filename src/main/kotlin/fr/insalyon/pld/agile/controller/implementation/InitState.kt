package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.util.Logger

/**
 * The initial state of the application
 */
class InitState : DefaultState<Any>() {
  override fun init(controller: Controller, element: Any) {
    Logger.info("Etat actuel = INIT_STATE")
    //Config.loadLastPlan()
    super.init(controller, element)
  }
}