package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.view.AlternativeHome

class Controller(val window: AlternativeHome) {

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

  val INIT_STATE: State<Any> = InitState()
  val LOADED_PLAN_STATE: State<Plan> = LoadedPlanState()
  val LOADED_DELIVERIES_STATE: State<RoundRequest> = LoadedDeliveriesState()
  val CALCULATED_ROUND_STATE: State<Round> = CalculatedRoundState()
  val ERROR_STATE: State<Pair<Exception, State<Nothing>>> = ErrorState()

  private var currentState: State<Nothing> = INIT_STATE

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

  fun ok() {
    currentState.ok(this)
  }

  fun undo(commands: List<Command>) {
    currentState.undo(this, commands)
  }

  fun redo(commands: List<Command>) {
    currentState.redo(this, commands)
  }

  private fun Exception.catchWithErrorState() {
    ERROR_STATE.init(this@Controller, Pair(this, currentState))
    currentState = ERROR_STATE
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