package fr.insalyon.pld.agile.controller.api

import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Intersection
import java.io.File

/**
 * A state of the application
 */
interface State<in T> {

  fun init(controller: Controller, element: T) {}

  /**
   * load the map of a city
   */
  fun loadPlan(controller: Controller)

  /**
   * load the map of a city with file path
   */
  fun loadPlan(controller: Controller, file: File)

  /**
   * Load the round request
   */
  fun loadRoundRequest(controller: Controller)

  /**
   * Load the round request
   */
  fun loadRoundRequest(controller: Controller, file: File)

  /**
   * Calculate the round of the loaded round request
   * @param controller
   */
  fun calculateRound(controller: Controller)

  /**
   * Remove the delivery from the controller's round
   */
  fun deleteDelivery(controller: Controller, delivery: Delivery)

  /**
   * Edit the delivery in the controller's round
   */
  fun editDelivery(controller: Controller, prevDelivery: Delivery, newDelivery: Delivery)

  /**
   * Open a popup to edit a delivery
   */

  fun openEditPopUp(controller: Controller, delivery: Delivery)

  /**
   * Open a popup to add a delivery
   */

  fun openAddPopUp(controller: Controller, intersection: Intersection)

  /**
   * Save the new delivery in the controller's round
   */
  fun saveDelivery(controller: Controller, delivery: Delivery)

  /**
   * The action to perform when was called
   */
  fun ok(controller: Controller)

  /**
   * The action to perform when undo was called
   */
  fun undo(controller: Controller, commands: List<Command>)

  /**
   * The action to perform when redo was called
   */
  fun redo(controller: Controller, commands: List<Command>)
}