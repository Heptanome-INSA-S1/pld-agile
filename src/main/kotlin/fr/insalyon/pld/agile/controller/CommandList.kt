package fr.insalyon.pld.agile.controller

import fr.insalyon.pld.agile.controller.api.Command

/**
 * The commands container
 */
class CommandList {

  private val commands = mutableListOf<Command>()
  private var indexOfCurrentCommand = -1
  private var lastIndexOfCommand = -1

  /**
   * Add and execute a command
   */
  fun add(command: Command) {
    if(commands.size == indexOfCurrentCommand + 1) {
      commands += command
      indexOfCurrentCommand += 1
    } else {
      commands[++indexOfCurrentCommand] = command
    }
    command.doCommand()
    lastIndexOfCommand = indexOfCurrentCommand
  }

  /**
   * Undo the last command
   */
  fun undo() {
    if(indexOfCurrentCommand < 0) return
    commands[indexOfCurrentCommand--].undoCommand()
  }

  /**
   * Redo the last undone command
   */
  fun redo() {
    if(indexOfCurrentCommand == lastIndexOfCommand) return
    commands[++indexOfCurrentCommand].doCommand()
  }

  /**
   * Clear the command list (cannot undo/redo)
   */
  fun reset() {
    indexOfCurrentCommand = -1
    lastIndexOfCommand = -1
    commands.clear()
  }

}