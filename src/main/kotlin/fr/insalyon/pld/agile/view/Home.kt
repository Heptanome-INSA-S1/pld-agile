package fr.insalyon.pld.agile.view


import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.util.Logger
import fr.insalyon.pld.agile.view.fragment.PlanFragment
import fr.insalyon.pld.agile.view.fragment.RoundFragment
import fr.insalyon.pld.agile.view.fragment.TimelineFragment
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.layout.HBox
import javafx.stage.Modality
import tornadofx.*
import javafx.scene.layout.StackPane

/**
 * Default home screen
 */
class Home : View() {
  override val root: BorderPane by fxml("/view/Home.fxml")
  private val loadPlanButton: Button by fxid()
  private val loadRoundRequestButton: Button by fxid()
  private val loadPlanMenuItem: MenuItem by fxid()
  private val loadRoundRequestMenuItem: MenuItem by fxid()
  private val centerBox: StackPane by fxid()
  private val rightBox: VBox by fxid()
  private val bottomBox: HBox by fxid()
  private val progressIndicator = ProgressIndicator()

  val controller: Controller = Controller(this)

  init {
    primaryStage.icons.add(Image("/image/app-icon.png"))
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

  fun refreshPlan() {
    centerBox.clear()
    Logger.info("Plan is printed")
    centerBox.add(PlanFragment::class, mapOf(
        PlanFragment::parentView to root,
        PlanFragment::plan to controller.plan))
    rightBox.clear()
    rightBox.add(loadRoundRequestButton)

    bottomBox.clear()
  }

  fun refreshRound() {
    centerBox.clear()
    centerBox.add(PlanFragment::class, mapOf(
          PlanFragment::parentView to root,
          PlanFragment::round to controller.round,
          PlanFragment::plan to controller.plan))
    rightBox.clear()
    rightBox.add(RoundFragment::class, mapOf(
        RoundFragment::parentView to root,
        RoundFragment::round to controller.round))
    bottomBox.clear()
    bottomBox.add(TimelineFragment::class, mapOf(
    TimelineFragment::parentView to root,
    TimelineFragment::round to controller.round,
    TimelineFragment::planSize to Pair<Double, Double>(controller.plan!!.width.toDouble(), controller.plan!!.height.toDouble())
    ))
  }

  fun errorPopUp(message: String?) {
    val alert = Alert(AlertType.ERROR)
    alert.title = "Erreur"
    alert.headerText = "Une erreur est survenu :"
    alert.contentText = message
    alert.initOwner(this.currentWindow)
    alert.initModality(Modality.APPLICATION_MODAL)

    alert.showAndWait()
    if (alert.result == ButtonType.OK) {
      controller.ok()
    }
  }

  fun loadingPlan() {
    centerBox.clear()
    centerBox.add(progressIndicator)
  }

  fun loadingRound() {
    rightBox.clear()
    rightBox.add(progressIndicator)
  }
}
