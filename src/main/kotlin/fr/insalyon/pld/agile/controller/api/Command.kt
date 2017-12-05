package fr.insalyon.pld.agile.controller.api

interface Command {
    fun doCommand()
    fun undoCommand()
}