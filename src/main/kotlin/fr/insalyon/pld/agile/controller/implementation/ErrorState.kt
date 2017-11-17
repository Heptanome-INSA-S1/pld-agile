package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State

class ErrorState : DefaultState<Pair<Exception, State<Nothing>>>(), State<Pair<Exception, State<Nothing>>> {

  var previousState: State<Nothing> = this

  override fun init(element: Pair<Exception, State<Nothing>>) {
    val exception = element.first
    previousState = element.second
    System.err.println(exception.toString())
  }

  override fun ok(controller: Controller) {
    controller.changeState(previousState)
  }

}