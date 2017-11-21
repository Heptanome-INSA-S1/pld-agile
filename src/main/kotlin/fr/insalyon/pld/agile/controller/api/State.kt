package fr.insalyon.pld.agile.controller.api

import fr.insalyon.pld.agile.controller.implementation.Controller
import java.io.File

interface State<in T> {

  fun init(controller: Controller, element: T) {}

  /**
   * load the map of a city
   * @param controller
   */
  fun loadPlan(controller: Controller)

  /**
   * load the map of a city with file path
   * @param controller
   */
  fun loadPlan(controller: Controller, file: File)

  /**
   * Load the round request
   * @param controller
   */
  fun loadRoundRequest(controller: Controller)

  /**
   * Load the round request
   * @param controller
   */
  fun loadRoundRequest(controller: Controller, file: File)

  /**
   * Calculate the round of the loaded round request
   * @param controller
   */
  fun calculateRound(controller: Controller)

  /**
   *
   */
  fun ok(controller: Controller)

  /**
   *
   */
  fun undo(controller: Controller, commands: List<Command>)

  /**
   *
   */
  fun redo(controller: Controller, commands: List<Command>)
}