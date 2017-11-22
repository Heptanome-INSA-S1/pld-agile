package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State

class InitState : DefaultState<Any>() {
  override fun init(controller: Controller, element: Any) {
    println("Etat actuelle = INIT_STATE")
    super.init(controller, element)
  }
}