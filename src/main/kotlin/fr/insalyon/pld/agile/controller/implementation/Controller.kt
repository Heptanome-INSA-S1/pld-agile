package fr.insalyon.pld.agile.controller.implementation

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.controller.CommandList
import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.controller.commands.DebugCommand
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.util.Logger
import fr.insalyon.pld.agile.view.Home
import fr.insalyon.pld.agile.view.fragment.DeliveryAdd
import fr.insalyon.pld.agile.view.fragment.DeliveryEditor
import tornadofx.*
import java.io.File

/**
 * The controller of the application
 */
class Controller(val window: Home) {

  var plan: Plan? = null
  protected set(value) {
    roundRequest = null
    round = null
    field = value
  }
  var roundRequest: RoundRequest? = null
  protected set(value) {
    round = null
    field = value
  }
  var round: Round? = null
  protected set

  val commands = CommandList()

  val INIT_STATE: State<Any> = InitState()
  val LOADED_PLAN_STATE: State<Plan> = LoadedPlanState()
  val LOADED_DELIVERIES_STATE: State<RoundRequest> = LoadedDeliveriesState()
  val CALCULATED_ROUND_STATE: State<Round> = CalculatedRoundState()
  val ERROR_STATE: State<Pair<Exception, State<Nothing>>> = ErrorState()
  val EDITING_DELIVERY_STATE: State<Delivery> = EditingDeliveryState()
  val CREATE_DELIVERY_STATE: State<Intersection> = CreateDeliveryState()

  private var currentState: State<Nothing> = INIT_STATE

  init {
    commands.add(DebugCommand())
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

  fun editingOfDelivery(delivery: Delivery){
    try {
      currentState.openEditPopUp(this, delivery)
    } catch (e: Exception) {
      e.catchWithErrorState()
    }
  }

  fun editDelivery(parentView: Fragment, prevDelivery: Delivery, newDelivery: Delivery){
    try {
      currentState.editDelivery(this, prevDelivery, newDelivery)
      parentView.close()
    } catch (e: Exception) {
      e.catchWithErrorState()
    }
  }

  fun addDelivery(intersection: Intersection){
    try {
      currentState.openAddPopUp(this, intersection)
    } catch (e: Exception) {
      e.catchWithErrorState()
    }
  }

  fun saveNewDelivery(parentView: Fragment, delivery : Delivery){
    try {
      currentState.saveDelivery(this, delivery)
      parentView.close()
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

  // Extension functions
  private fun Exception.catchWithErrorState() {
    val previousState = currentState
    currentState = ERROR_STATE
    ERROR_STATE.init(this@Controller, Pair(this, previousState))
  }

  fun closeDeliveryEditor(view: DeliveryEditor) {
    view.close()
    changeState(CALCULATED_ROUND_STATE)
  }

  fun closeDeliveryAdd(view: DeliveryAdd) {
    view.close()
    changeState(CALCULATED_ROUND_STATE)
  }

}