package fr.insalyon.pld.agile.view


import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.view.fragments.PlanFragment
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import tornadofx.*

/**
 * Default home screen
 */
class Home : View() {
  override val root: BorderPane by fxml("/view/Home.fxml")
  // Map the current view to resources/view/Home.fxml
  val childView: PlanFragment by param()
  val loadPlanButton: Button by fxid()
  val loadPlanMenuItem: MenuItem by fxid()
  val loadRoundRequestMenuItem: MenuItem by fxid()
  val centerVBox: VBox by fxid()

  val controller: Controller = fr.insalyon.pld.agile.controller.implementation.Controller(this)

  init {
    loadPlanButton.setOnAction {
      controller.loadPlan()
      planView()
    }

    loadPlanMenuItem.setOnAction {
      controller.loadPlan()
      planView()
    }

    loadRoundRequestMenuItem.setOnAction {
      controller.loadRoundRequest()
      controller.calculateRound()
      println(controller.round)
      addDeliveries(controller.round)
    }
  }

  fun planView() {

    root.center {
      add(PlanFragment::class, mapOf(
          PlanFragment::parentView to this,
          PlanFragment::plan to controller.plan))
      //replaceWith(find<PlanFragment>(PlanFragment::class, plan))
      //centerVBox.replaceWith(find<PlanFragment>(mapOf(PlanFragment::plan to plan)))
    }
  }

  fun addDeliveries(round : Round?) {
   root.center {
     add(PlanFragment::class, mapOf(
             PlanFragment::parentView to this,
             PlanFragment::round to round,
             PlanFragment::plan to controller.plan))
   }
  }
}
