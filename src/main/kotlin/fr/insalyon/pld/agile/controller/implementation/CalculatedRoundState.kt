package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.seconds
import fr.insalyon.pld.agile.util.Logger
import java.io.File

/**
 * The application state when a round has been computed
 */
class CalculatedRoundState : DefaultState<Round>(), State<Round> {

  override fun init(controller: Controller, element: Round) {
    controller.round = element
    Logger.info(controller.round!!.warehouse.departureHour + controller.round!!.length.seconds)
    controller.window.refreshAll()
  }

  override fun loadRoundRequest(controller: Controller) {
    defaultLoadRoundRequestImpl(controller)
  }

  override fun loadRoundRequest(controller: Controller, file : File) {
    fileLoadRoundRequestImpl(controller, file)
  }

  override fun calculateRound(controller: Controller) {
    try {
      defaultCalculateRoundImpl(controller)
    } catch (e: Exception) {
      controller.manageException(e)
    }
  }

  override fun deleteDelivery(controller: Controller, delivery: Delivery) {
    if(controller.round!!.deliveries().size == 1) throw IllegalStateException("Cannot delete the last delivery")
    defaultDeleteDelivery(controller, delivery)
  }

  override fun openAddPopUp(controller: Controller, intersection: Intersection) {
    controller.changeStateAndInit(controller.CREATE_DELIVERY_STATE, intersection)
  }

  override fun openEditPopUp(controller: Controller, delivery: Delivery) {
    controller.changeStateAndInit(controller.EDITING_DELIVERY_STATE, delivery)
  }

}
