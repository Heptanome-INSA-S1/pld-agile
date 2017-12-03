package fr.insalyon.pld.agile.controller

import fr.insalyon.pld.agile.controller.api.Command

class CommandList {

  private val commands = mutableListOf<Command>()
  private var indexOfCurrentCommand = -1
  private var lastIndexOfCommand = -1

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

  fun undo() {
    if(indexOfCurrentCommand < 0) return
    commands[indexOfCurrentCommand--].undoCommand()
  }

  fun redo() {
    if(indexOfCurrentCommand == lastIndexOfCommand) return
    commands[++indexOfCurrentCommand].doCommand()
  }

  fun reset() {
    indexOfCurrentCommand = -1
    lastIndexOfCommand = -1
    commands.clear()
  }

}