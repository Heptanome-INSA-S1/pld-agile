package fr.insalyon.pld.agile.controller.api

import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.RoundRequest

interface State<in T> {

  val plan: Plan?
  val roundRequest: RoundRequest?
  val round: Round?

  fun init(element: T, window: Any) {}

  /**
   * load the map of a city
   * @param controller
   */
  fun loadPlan(controller: Controller)

  /**
   * Load the round request
   * @param controller
   */
  fun loadRoundRequest(controller: Controller)

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