package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.util.Logger

/**
 * The state when editing mode is enable
 */
class EditingDeliveryState: DefaultState<Delivery>(){
  override fun init(controller: Controller, element: Delivery) {
    Logger.info("Etat actuel : EDITING_DELIVERY_STATE")
    controller.window.openEditor(element)
  }

  override fun editDelivery(controller: Controller, prevDelivery: Delivery, newDelivery: Delivery) {
    defaultEditDelivery(controller, prevDelivery, newDelivery)
  }
}