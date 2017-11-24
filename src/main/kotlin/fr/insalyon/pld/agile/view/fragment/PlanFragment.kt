package fr.insalyon.pld.agile.view.fragment
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.view.event.HighlightLocationEvent
import fr.insalyon.pld.agile.view.event.HighlightLocationInListEvent
import javafx.scene.control.ScrollPane

import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import tornadofx.*

const val UP : Int = 0
const val RIGHT : Int = 1
const val DOWN : Int = 2
const val LEFT : Int = 3
const val CENTER : Int = 4

class PlanFragment : Fragment(){
  val parentView: BorderPane by param()
  val plan: Plan by param()
  val round: Round? by param()

  val MAP_SIZE: Double by param(800.0)

  var SIZE = 1.0

  val OFFSET_SIZE : Double = MAP_SIZE/10.0

  val shapeGroup = group {
    plan.nodes.forEach {
      val nodeX: Double = (it.element.x / (plan.width * 1.0) * MAP_SIZE)
      val nodeY: Double = (it.element.y / (plan.height * 1.0) * MAP_SIZE)
      circle {
        centerX = nodeY
        centerY = -1.0*nodeX
        radius = SIZE
        fill = Color.WHITE
      }
      plan.outEdges[it.index].forEach {
        val endNodeX: Double = (it.to.element.x / (plan.width * 1.0) * MAP_SIZE)
        val endNodeY: Double = (it.to.element.y / (plan.height * 1.0) * MAP_SIZE)
        line {
          startX = nodeY
          startY = nodeX*-1.0
          endX = endNodeY
          endY = endNodeX*-1.0
          stroke = Color.WHITE
        }
      }
    }

    if(round!=null){
      val notNullRound = round!!
      circle {
        centerX = notNullRound.warehouse.address.y / (plan.height * 1.0) * MAP_SIZE
        centerY = notNullRound.warehouse.address.x / (plan.width * 1.0) * MAP_SIZE *-1.0
        radius = SIZE * 7
        fill = Color.BROWN
        id = ""+notNullRound.warehouse.address.id
        onHover { fire(HighlightLocationInListEvent(id,Color.LIGHTGREEN)) }
        setOnMouseExited { fire(HighlightLocationInListEvent(id,Color.WHITE)) }
      }
      notNullRound.deliveries().forEach {
        val nodeX: Double = it.address.x / (plan.width * 1.0) * MAP_SIZE
        val nodeY: Double = it.address.y / (plan.height * 1.0) * MAP_SIZE
        circle {
          centerX = nodeY
          centerY = nodeX*-1.0
          radius = SIZE * 7
          fill = Color.GREEN
          id = ""+it.address.id
          onHover { fire(HighlightLocationInListEvent(id,Color.LIGHTGREEN)) }
          setOnMouseExited { fire(HighlightLocationInListEvent(id,Color.WHITE)) }
        }
      }
      notNullRound.path().forEach{

        var fromX: Double
        var fromY: Double
        var toX = 0.0
        var toY = 0.0
        var index = 0
        it.nodes.forEach{
          val nodeX: Double = it.x / (plan.width * 1.0) * MAP_SIZE
          val nodeY: Double = it.y / (plan.height * 1.0) * MAP_SIZE
          if(index>0){
            fromX = toX
            fromY = toY
            toX = nodeX
            toY = nodeY
            line {
              startY = fromX*-1.0
              startX = fromY
              endY = toX*-1.0
              endX = toY
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
  val scroll = scrollpane {
      hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
      vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
      style = "-fx-background: #3B3833;"
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
      shortcut("Ctrl+C",{
          move(CENTER)
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

  override val root = stackpane {
    add(scroll)
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
    println("Before -> ")
    println(" T_Y : "+ shapeGroup.translateY)
    println(" T_X : " + shapeGroup.translateX)
    println(" S_X : " + shapeGroup.scaleX)
    println(" S_Y : " + shapeGroup.scaleY)
    println(" Layout X " + shapeGroup.layoutX)
    println(" Layout Y " + shapeGroup.layoutY)
    println(scroll.viewportBounds)
    println(scroll.layoutBounds)
    println(scroll.boundsInLocal)
    println(scroll.boundsInParent)
    println("--------")
    println(shapeGroup.boundsInParent)
    println(shapeGroup.boundsInLocal)
    println(shapeGroup.layoutBounds)
    when(direction){
      UP -> {
        shapeGroup.translateY += OFFSET_SIZE
      }
      RIGHT -> {
          shapeGroup.translateX -= OFFSET_SIZE
      }
      DOWN -> {
          shapeGroup.translateY -= OFFSET_SIZE
      }
      LEFT -> {
          shapeGroup.translateX += OFFSET_SIZE
      }
      CENTER -> {
        shapeGroup.translateX = 0.0
        shapeGroup.translateY = 0.0
      }
    }

    println("After -> ")
    println(" T_Y : "+ shapeGroup.translateY)
    println(" T_X : " + shapeGroup.translateX)
    println(" S_X : " + shapeGroup.scaleX)
    println(" S_Y : " + shapeGroup.scaleY)
  }

    init {
        subscribe<HighlightLocationEvent> {
            event -> highlightLocation(event.id,event.isWarehouse)
        }
    }


    var idHighlight: String? =null
    var colorHighlight:Color =Color.GREEN

    private fun highlightLocation(id:String, isWarehouse:Boolean){
        println("highlight : "+id)
        shapeGroup.children
                .filter { it.id!=null && it.id.equals(id) }
                .forEach {
                    it.scaleX = 3.0
                    it.scaleY = 3.0
                    it.style {
                        fill = Color.CYAN
                    }
                }
        if(idHighlight!=null) {
            println("lowlight : "+idHighlight)
            shapeGroup.children
                    .filter { it.id!=null && it.id.equals(idHighlight) }
                    .forEach {
                        it.scaleX = 1.0
                        it.scaleY = 1.0
                        it.style {
                            fill = colorHighlight
                        }
                    }
        }
        idHighlight=id
        colorHighlight= if(isWarehouse) Color.RED else Color.GREEN
    }

}