package fr.insalyon.pld.agile.view


import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.view.fragment.PlanFragment
import fr.insalyon.pld.agile.view.fragment.RoundFragment
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import tornadofx.*

/**
 * Default home screen
 */
class Home : View() {
  override val root: BorderPane by fxml("/view/Home.fxml")
  private val loadPlanButton: Button by fxid()
  private val loadRoundRequestButton: Button by fxid()
  private val loadPlanMenuItem: MenuItem by fxid()
  private val loadRoundRequestMenuItem: MenuItem by fxid()

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

    loadRoundRequestButton.setOnAction {
      controller.loadRoundRequest()
      controller.calculateRound()
      roundView()
    }

    loadRoundRequestMenuItem.setOnAction {
      controller.loadRoundRequest()
      controller.calculateRound()
      roundView()
    }
  }

  private fun planView() {
    root.center {
      add(PlanFragment::class, mapOf(
          PlanFragment::parentView to this,
          PlanFragment::plan to controller.plan))
    }
  }

  private fun roundView() {
    root.center {
      add(PlanFragment::class, mapOf(
          PlanFragment::parentView to this,
          PlanFragment::round to controller.round,
          PlanFragment::plan to controller.plan))
    }
    root.right {
      add(RoundFragment::class, mapOf(
              RoundFragment::parentView to this,
              RoundFragment::round to controller.round))
    }
  }
}
