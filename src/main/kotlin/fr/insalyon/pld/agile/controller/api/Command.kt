package fr.insalyon.pld.agile.controller.api

interface Command {
    //dqz
    fun doCommand()
    fun undoCommand()
}