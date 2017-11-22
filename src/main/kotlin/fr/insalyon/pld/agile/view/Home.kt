package fr.insalyon.pld.agile.view


import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.view.fragment.PlanFragment
import fr.insalyon.pld.agile.view.fragment.RoundFragment
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*
import java.io.File
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode


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

  val controller: Controller = fr.insalyon.pld.agile.controller.implementation.Controller(this)

  init {

    root.setOnDragOver { event ->
      val db = event.dragboard
      if (db.hasFiles() && db.files.size == 1) {
        event.acceptTransferModes(TransferMode.COPY)
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
        planView()
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
        controller.calculateRound()
        roundView()
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
      controller.calculateRound()
    }

    loadRoundRequestMenuItem.setOnAction {
      controller.loadRoundRequest()
      controller.calculateRound()
    }
  }

  fun planView() {
    centerBox.clear()
    centerBox.add(PlanFragment::class, mapOf(
        PlanFragment::parentView to this,
        PlanFragment::plan to controller.plan))
    rightBox.clear()
    rightBox.add(loadPlanButton)
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
}
