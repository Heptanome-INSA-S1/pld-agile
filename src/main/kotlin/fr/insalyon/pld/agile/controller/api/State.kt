package fr.insalyon.pld.agile.controller.api

import fr.insalyon.pld.agile.model.Round
import java.io.File

interface State {
    /**
     *
     */
    fun loadPlan(file: File) : Boolean

    /**
     *
     */
    fun loadRoundRequest(file: File) : Boolean

    /**
     *
     */
    fun calculateRound() : Round

    /**
     *
     */
    fun ok(state : State) : State

    /**
     *
     */
    fun undo(commands: List<Command>) : List<Command>

    /**
     *
     */
    fun redo(commands: List<Command>) : List<Command>
}