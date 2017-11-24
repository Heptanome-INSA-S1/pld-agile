package fr.insalyon.pld.agile.view


import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.view.fragment.PlanFragment
import fr.insalyon.pld.agile.view.fragment.RoundFragment
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import tornadofx.*
import javafx.scene.input.TransferMode
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.Pane
import javafx.stage.Modality


/**
 * Default home screen
 */
class Home : View() {
  override val root: BorderPane by fxml("/view/Home.fxml")
  private val loadPlanButton: Button by fxid()
  private val loadRoundRequestButton: Button by fxid()
  private val loadPlanMenuItem: MenuItem by fxid()
  private val loadRoundRequestMenuItem: MenuItem by fxid()
  private val centerBox: VBox by fxid()
  private val rightBox: VBox by fxid()

  val controller: Controller = Controller(this)

  init {

    root.setOnDragOver { event ->
      val db = event.dragboard
      if (db.hasFiles() && db.files.size == 1) {
        event.acceptTransferModes(TransferMode.MOVE)
      } else {
        event.consume()
      }
    }

    centerBox.setOnDragDropped { event ->
      val db = event.dragboard
      var success = false
      if (db.hasFiles()) {
        success = true
        controller.loadPlan(db.files[0])
      }
      event.isDropCompleted = success
      event.consume()
    }

    rightBox.setOnDragDropped { event ->
      val db = event.dragboard
      var success = false
      if (db.hasFiles()) {
        success = true
        controller.loadRoundRequest(db.files[0])
      }
      event.isDropCompleted = success
      event.consume()
    }

    loadPlanButton.setOnAction {
      controller.loadPlan()
    }

    loadPlanMenuItem.setOnAction {
      controller.loadPlan()
    }

    loadRoundRequestButton.setOnAction {
      controller.loadRoundRequest()
    }

    loadRoundRequestMenuItem.setOnAction {
      controller.loadRoundRequest()
    }
  }

  fun planView() {
    centerBox.clear()
    centerBox.add(PlanFragment::class, mapOf(
        PlanFragment::parentView to this,
        PlanFragment::plan to controller.plan))
    rightBox.clear()
    rightBox.add(loadRoundRequestButton)
  }

  fun roundView() {
    centerBox.clear()
    centerBox.add(PlanFragment::class, mapOf(
          PlanFragment::parentView to this,
          PlanFragment::round to controller.round,
          PlanFragment::plan to controller.plan))

    rightBox.clear()
    rightBox.add(RoundFragment::class, mapOf(
              RoundFragment::parentView to this,
              RoundFragment::round to controller.round))
  }

  fun errorPopUp(message : String?) {
    val alert = Alert(AlertType.ERROR)
    alert.title = "Erreur"
    alert.headerText = "Une erreur est survenu :"
    alert.contentText = message
    alert.initOwner(this.currentWindow)
    alert.initModality(Modality.APPLICATION_MODAL)

    alert.showAndWait()
    if (alert.getResult() == ButtonType.OK){
      controller.ok()
    }
  }

  fun loadingPlan(){
    centerBox.clear()
    centerBox.add(ProgressIndicator())
  }

  fun loadingRound(){
    rightBox.clear()
    rightBox.add(ProgressIndicator())
  }
}
