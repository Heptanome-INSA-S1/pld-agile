package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import java.io.File

class Controller {

    companion object {
        val DEFAULT_STATE = DefaultState()
    }

    private var plan: Plan? = null
    private var currentState = DEFAULT_STATE

    fun loadPlan(pathFile: String) {
        try {
            plan = currentState.loadPlan(pathFile)
        } catch (e: Exception) {
          //  currentState = ErrorState()
        }
    }

    fun loadRoundRequest(file: File){
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun calculateRound(): Round {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun ok(state: State): State {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun undo(commands: List<Command>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun redo(commands: List<Command>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}