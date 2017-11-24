package fr.insalyon.pld.agile.controller.implementation

class InitState : DefaultState<Any>() {
  override fun init(controller: Controller, element: Any) {
    println("Etat actuel = INIT_STATE")
    super.init(controller, element)
  }
}