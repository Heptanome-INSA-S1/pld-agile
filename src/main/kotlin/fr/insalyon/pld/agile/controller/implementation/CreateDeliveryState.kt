package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.util.Logger

class CreateDeliveryState : DefaultState<Intersection>(){
  override fun init(controller: Controller, element: Intersection) {
    Logger.info("Etat actuelle : CREATE_DELIVERY_STATE")
    controller.window.openEditorNew(element)
  }

  override fun saveDelivery(controller: Controller, delivery: Delivery) {
    defaultSaveDelivery(controller, delivery)
  }
}