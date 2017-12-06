package fr.insalyon.pld.agile.controller.api

/**
 * Command interface. A command can be done or undone.
 */
interface Command {
  /**
   * The action of the command
   */
  fun doCommand()

  /**
   * The reverse action of the command
   */
  fun undoCommand()
}