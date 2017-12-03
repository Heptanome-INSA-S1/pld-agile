package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.controller.CommandList
import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.controller.commands.DebugCommand
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.util.Logger
import fr.insalyon.pld.agile.view.Home
import java.io.File

class Controller(val window: Home) {

  internal var plan: Plan? = null
  set(value) {
    roundRequest = null
    round = null
    field = value
  }
  internal var roundRequest: RoundRequest? = null
  set(value) {
    round = null
    field = value
  }
  internal var round: Round? = null
  internal val commands = CommandList()

  val INIT_STATE: State<Any> = InitState()
  val LOADED_PLAN_STATE: State<Plan> = LoadedPlanState()
  val LOADED_DELIVERIES_STATE: State<RoundRequest> = LoadedDeliveriesState()
  val CALCULATED_ROUND_STATE: State<Round> = CalculatedRoundState()
  val ERROR_STATE: State<Pair<Exception, State<Nothing>>> = ErrorState()

  private var currentState: State<Nothing> = INIT_STATE

  init {
    if(Config.LOGGER_LEVEL == Logger.Level.DEBUG) {
      commands.add(DebugCommand())
    }
  }

  fun loadPlan(file: File){
    try {
      currentState.loadPlan(this, file)
    } catch (e: Exception) {
      e.catchWithErrorState()
    }
  }
  fun loadPlan() {
    try {
      currentState.loadPlan(this)
    } catch (e: Exception) {
      e.catchWithErrorState()
    }
  }

  fun loadRoundRequest() {
    try {
      currentState.loadRoundRequest(this)
      calculateRound()
    } catch (e: Exception) {
      e.catchWithErrorState()
    }
  }

  fun loadRoundRequest(file: File) {
    try {
      currentState.loadRoundRequest(this, file)
      calculateRound()
    } catch (e: Exception) {
      e.catchWithErrorState()
    }
  }

  fun calculateRound() {
    try {
      currentState.calculateRound(this)
    } catch (e: Exception) {
      e.catchWithErrorState()
    }
  }

  fun deleteDelivery(delivery: Delivery) {
    try {
      currentState.deleteDelivery(this, delivery)
    } catch (e: Exception) {
      e.catchWithErrorState()
    }
  }

  fun ok() {
    currentState.ok(this)
  }

  fun undo() {
    commands.undo()
  }

  fun redo() {
    commands.redo()
  }

  private fun Exception.catchWithErrorState() {
    val previousState = currentState
    currentState = ERROR_STATE
    ERROR_STATE.init(this@Controller, Pair(this, previousState))
  }

  fun manageException(e: Exception) {
    e.catchWithErrorState()
  }

  fun <T> changeStateAndInit(nextState: State<T>, initParam: T) {
    nextState.init(this, initParam)
    currentState = nextState
  }

  fun <T> changeState(nextState: State<T>) {
    currentState = nextState
  }

}