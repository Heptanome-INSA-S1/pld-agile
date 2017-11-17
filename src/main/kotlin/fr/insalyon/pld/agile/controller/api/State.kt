package fr.insalyon.pld.agile.controller.api

import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.RoundRequest
import java.io.File

interface State<in T> {

  val plan: Plan?
  val roundRequest: RoundRequest?
  val round: Round?

  fun init(element: T) {}

  /**
   * qdq
   */
  fun loadPlan(controller: Controller, pathFile: String)

  /**
   *
   */
  fun loadRoundRequest(controller: Controller, file: File)

  /**
   *
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