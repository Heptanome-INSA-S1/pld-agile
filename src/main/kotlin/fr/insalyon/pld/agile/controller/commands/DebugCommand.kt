package fr.insalyon.pld.agile.controller.commands

import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.util.Logger

/**
 * A command for debug the Command List
 */
class DebugCommand : Command {
  override fun doCommand() {
    Logger.debug("Do debug command")
  }

  override fun undoCommand() {
    Logger.debug("Undo debug command")
  }
}