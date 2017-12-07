package fr.insalyon.pld.agile.view


import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.getResource
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.util.Logger
import fr.insalyon.pld.agile.view.fragment.*
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Modality
import tornadofx.*
import java.io.File

/**
 * Default home screen
 */
class Home : View() {
  override val root: BorderPane by fxml("/view/Home.fxml")

  // Buttons
  private val loadPlanButton: Button by fxid()
  private val loadRoundRequestButton: Button by fxid()
  private val loadPlanMenuItem: MenuItem by fxid()
  private val loadRoundRequestMenuItem: MenuItem by fxid()
  private val undo: MenuItem by fxid()
  private val redo: MenuItem by fxid()

  // Canvas
  private val centerBox: StackPane by fxid()
  private val rightBox: VBox by fxid()
  private val bottomBox: HBox by fxid()
  private val progressIndicator = ProgressIndicator()

  val controller: Controller = Controller(this)

  init {
    primaryStage.isMaximized = Config.Util.WINDOW_IS_MAX_SIZE
    primaryStage.icons.add(Image("/image/app-icon.png"))
    root.setOnDragOver { event ->
      val db = event.dragboard
      if (db.hasFiles() && db.files.size == 1) {
        event.acceptTransferModes(TransferMode.MOVE)
      } else {
        event.consume()
      }
    }

    root.widthProperty().addListener{ _,_,_ -> refreshTimeLine() }

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

    redo.setOnAction {
      controller.redo()
    }

    undo.setOnAction {
      controller.undo()
    }

    shortcut("Ctrl+Z"){
      controller.undo()
    }

    shortcut("Ctrl+Y"){
      controller.redo()
    }

    if(Config.loadLastPlan().isNotEmpty()){
      val lastFile = File(Config.loadLastPlan())
      if(lastFile.exists()){
        controller.loadPlan(lastFile)
      }
    }

  }

  fun refreshAll() {
    refreshPlan()
    refreshRound()
    refreshTimeLine()
  }

  fun refreshPlan() {
    centerBox.clear()
    Logger.info("Plan is refreshed")
    centerBox.add(PlanFragment::class, mapOf(
        PlanFragment::parentView to root,
        PlanFragment::controller to controller,
        PlanFragment::round to controller.round,
        PlanFragment::plan to controller.plan))
  }

  fun refreshTimeLine() {
    println(controller.round?.toTrace())
    Logger.info("Timeline is refreshed")
    if(controller.round == null) {
      bottomBox.clear()
    } else {
      bottomBox.clear()
      bottomBox.add(TimelineFragment::class, mapOf(
          TimelineFragment::parentView to root,
          TimelineFragment::round to controller.round!!
      ))
    }
  }

  fun refreshRound() {
    Logger.info("Round is refreshed")
    if(controller.round == null) {
      rightBox.clear()
      rightBox.add(loadRoundRequestButton)
    } else {
      rightBox.clear()
      rightBox.add(RoundFragment::class, mapOf(
          RoundFragment::parentView to root,
          RoundFragment::controller to controller))
    }
  }

  fun errorPopUp(message: String?) {
    val alert = Alert(AlertType.ERROR)
    alert.title = "Erreur"
    alert.headerText = "Une erreur est survenu :"
    alert.contentText = message
    alert.initOwner(currentWindow)
    alert.initModality(Modality.APPLICATION_MODAL)

    alert.showAndWait()
    if (alert.result == ButtonType.OK) {
      controller.ok()
    }
  }

  fun openEditor(delivery: Delivery){
    openInternalWindow(DeliveryEditor::class, params = mapOf(
        DeliveryEditor::prevDelivery to delivery,
        DeliveryEditor::parentView to this))
  }

  fun openEditorNew(intersection: Intersection){
    openInternalWindow(DeliveryAdd::class, params = mapOf(
        DeliveryAdd::intersection to intersection,
        DeliveryAdd::parentView to this))
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
