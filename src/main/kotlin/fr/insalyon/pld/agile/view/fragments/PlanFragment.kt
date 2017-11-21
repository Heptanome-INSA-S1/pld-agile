package fr.insalyon.pld.agile.view.fragments

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.Stylesheet.Companion.line

class PlanFragment : Fragment() {
  val parentView: BorderPane by param()
  val plan: Plan by param()
  val round: Round? by param()
  val X_SIZE: Double by param(800.0)
  val Y_SIZE: Double by param(800.0)
  var SIZE = 1.0

  private val stack = stackpane {
    scrollpane {
      val group = group {
        plan.nodes.forEach {
          val nodeX: Double = it.element.x / (plan.width * 1.0) * X_SIZE
          val nodeY: Double = it.element.y / (plan.height * 1.0) * Y_SIZE
          circle {
            centerX = nodeX
            centerY = nodeY
            radius = SIZE
          }
          plan.outEdges[it.index].forEach {
            line {
              startX = nodeX
              startY = nodeY
              endX = it.to.element.x / (plan.width * 1.0) * X_SIZE
              endY = it.to.element.y / (plan.height * 1.0) * Y_SIZE
            }
          }
        }
        if(round!=null){
          val notNullRound = round!!
          circle {
            centerX = notNullRound.warehouse.address.x / (plan.width * 1.0) * X_SIZE
            centerY = notNullRound.warehouse.address.y / (plan.height * 1.0) * Y_SIZE
            radius = SIZE * 5
            fill = Color.BROWN
          }
          notNullRound.deliveries().forEach {
            val nodeX: Double = it.address.x / (plan.width * 1.0) * X_SIZE
            val nodeY: Double = it.address.y / (plan.height * 1.0) * Y_SIZE
            circle {
              centerX = nodeX
              centerY = nodeY
              radius = SIZE * 5
              fill = Color.GREEN
            }
          }
          notNullRound.path().forEach{

            var fromX: Double = 0.0
            var fromY: Double = 0.0
            var toX: Double = 0.0
            var toY: Double = 0.0
            var index: Int = 0
            it.nodes.forEach{
              val nodeX: Double = it.x / (plan.width * 1.0) * X_SIZE
              val nodeY: Double = it.y / (plan.height * 1.0) * Y_SIZE
              circle {
                  centerX = nodeX
                  centerY = nodeY
                  radius = SIZE * 3
                  fill = Color.YELLOW
              }
              if(index>0){
                fromX = toX
                fromY = toY
                toX = nodeX
                toY = nodeY
                line {
                 startX = fromX
                 startY = fromY
                 endX = toX
                 endY = toY
                 stroke = Color.ORANGE
                }
              } else {
                toX = nodeX
                toY = nodeY
              }
              index++
            }
          }
        }
      }
    }
  }

  override val root = vbox {
    hbox {
      button("Zoom +") {
        action {
          zoomIn()
        }
      }
      button("Zoom -") {
        action {
          zoomOut()
        }
      }
    }
    hbox {
      slider(0.0, 1000.0) {

      }
    }
  }

  init {
    root.add(stack)
  }

  fun zoomIn() {
    parentView.center {
      add(PlanFragment::class, mapOf(
          PlanFragment::parentView to this,
          PlanFragment::plan to plan,
              PlanFragment::round to round,
          PlanFragment::X_SIZE to X_SIZE + 100.0,
          PlanFragment::Y_SIZE to Y_SIZE + 100.0))
      //replaceWith(find<PlanFragment>(PlanFragment::class, plan))
      //centerVBox.replaceWith(find<PlanFragment>(mapOf(PlanFragment::plan to plan)))
    }
  }

  fun zoomOut() {
    parentView.center {
      add(PlanFragment::class, mapOf(
          PlanFragment::parentView to this,
          PlanFragment::plan to plan,
              PlanFragment::round to round,
          PlanFragment::X_SIZE to X_SIZE - 100.0,
          PlanFragment::Y_SIZE to Y_SIZE - 100.0))
      //replaceWith(find<PlanFragment>(PlanFragment::class, plan))
      //centerVBox.replaceWith(find<PlanFragment>(mapOf(PlanFragment::plan to plan)))
    }
  }

}