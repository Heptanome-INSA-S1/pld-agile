package fr.insalyon.pld.agile.view.fragment
import fr.insalyon.pld.agile.model.Plan

import javafx.scene.layout.BorderPane
import tornadofx.*

const val UP : Int = 0
const val RIGHT : Int = 1
const val DOWN : Int = 2
const val LEFT : Int = 3

class PlanFragment : Fragment(){
  val parentView: BorderPane by param()
  val plan: Plan by param()

  val MAP_SIZE: Double by param(800.0)

  var SIZE = 1.0

  val OFFSET_SIZE : Double = MAP_SIZE/10.0

  val shapeGroup = group {
    plan.nodes.forEach {
      val nodeX: Double = (it.element.x / (plan.width * 1.0) * MAP_SIZE)
      val nodeY: Double = (it.element.y / (plan.height * 1.0) * MAP_SIZE)
      circle {
        centerX = nodeX
        centerY = nodeY
        radius = SIZE
      }
      plan.outEdges[it.index].forEach {
        val ligne = line {
          startX = nodeX
          startY = nodeY
          endX = (it.to.element.x / (plan.width * 1.0) * MAP_SIZE)
          endY = (it.to.element.y / (plan.height * 1.0) * MAP_SIZE)
        }
      }
    }
  }

  override val root = stackpane {
    scrollpane {
      add(shapeGroup)

      shortcut("Ctrl+UP",{
          move(UP)
      })
      shortcut("Ctrl+DOWN",{
          move(DOWN)
      })
      shortcut("Ctrl+RIGHT",{
          move(RIGHT)
      })
      shortcut("Ctrl+LEFT",{
          move(LEFT)
      })
      shortcut("Ctrl+Shift+UP",{
          zoomIn()
      })
      shortcut("Ctrl+Shift+DOWN",{
          zoomOut()
      })
      setOnKeyPressed { event ->
          println(event.character)
      }
   }
  }

  fun zoomIn() {
    shapeGroup.scaleX += 0.25
    shapeGroup.scaleY += 0.25
  }

  fun zoomOut() {
    shapeGroup.scaleX -= 0.25
    shapeGroup.scaleY -= 0.25
  }

  fun move(direction: Int){
      when(direction){
        UP -> {
            shapeGroup.translateY -= OFFSET_SIZE
        }
        RIGHT -> {
            shapeGroup.translateX += OFFSET_SIZE
        }
        DOWN -> {
            shapeGroup.translateY += OFFSET_SIZE
        }
        LEFT -> {
            shapeGroup.translateX -= OFFSET_SIZE
        }
      }
  }

}