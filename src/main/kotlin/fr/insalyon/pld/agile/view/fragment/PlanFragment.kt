package fr.insalyon.pld.agile.view.fragment
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.view.event.HighlightLocationEvent
import fr.insalyon.pld.agile.view.event.HighlightLocationInListEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent

import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import tornadofx.*
import javafx.scene.input.ScrollEvent



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

  private fun transform(x: Number, y: Number): Pair<Double, Double> {
    val transformedX = (y.toDouble() / (plan.height.toDouble()) * MAP_SIZE)
    val transformedY = (x.toDouble() / (plan.width.toDouble()) * MAP_SIZE) * -1.0
    return Pair(transformedX, transformedY)
  }

  val OFFSET_SIZE : Double = MAP_SIZE/10.0

  val shapeGroup = group {
    plan.nodes.forEach {
      val (nodeX, nodeY) = transform(it.element.x, it.element.y)
      circle {
        centerX = nodeX
        centerY = nodeY
        radius = SIZE
        fill = Color.WHITE
      }
      plan.outEdges[it.index].forEach {
        val (endNodeX,endNodeY: Double) = transform(it.to.element.x, it.to.element.y)
        line {
          startX = nodeX
          startY = nodeY
          endX = endNodeX
          endY = endNodeY
          stroke = Color.WHITE
        }
      }
    }

    if(round!=null){
      val notNullRound = round!!

      notNullRound.path().forEach{
        var fromX: Double
        var fromY: Double
        var toX = 0.0
        var toY = 0.0
        var index = 0
        it.nodes.forEach{
          val (nodeX, nodeY) = transform(it.x, it.y)
          if(index>0){
            fromX = toX
            fromY = toY
            toX = nodeX
            toY = nodeY
            line {
              startY = fromY
              startX = fromX
              endY = toY
              endX = toX
              stroke = Color.ORANGE
            }
          } else {
            toX = nodeX
            toY = nodeY
          }
          index++
        }
      }

      val (warehouseXPos, warehouseYPos) = transform(notNullRound.warehouse.address.x, notNullRound.warehouse.address.y)
      circle {
        centerX = warehouseXPos
        centerY = warehouseYPos
        radius = SIZE * 7
        fill = Color.BROWN
        id = notNullRound.warehouse.address.id.toString()
        onHover { fire(HighlightLocationInListEvent(id,Color.LIGHTCORAL)) }
        setOnMouseExited { fire(HighlightLocationInListEvent(id,Color.WHITE)) }
      }
      notNullRound.deliveries().forEachIndexed { index, it ->
        val (nodeX, nodeY) = transform(it.address.x, it.address.y)
        circle {
          centerX = nodeX
          centerY = nodeY
          radius = SIZE * 7
          fill = Color.GREEN
          id = it.address.id.toString()
          onHover { fire(HighlightLocationInListEvent(id,Color.LIGHTGREEN)) }
          setOnMouseExited { fire(HighlightLocationInListEvent(id,Color.WHITE)) }
        }

          label(""+(index+2)){
              if(index+2>9) {
                  layoutX = nodeX - 4
                  layoutY = nodeY - 5.5
              }else{
                  layoutX = nodeX - 2
                  layoutY = nodeY - 5.5
              }
              alignment= Pos.CENTER
              style{
                  fontSize=7. px
                  textFill=Color.LIGHTGREEN
              }
              val id = it.address.id.toString()
              //à optimiser ? deux events : un pour le cercle, un pour le label
              onHover { fire(HighlightLocationInListEvent(id,Color.LIGHTGREEN)) }
              setOnMouseExited { fire(HighlightLocationInListEvent(id,Color.WHITE)) }
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
      addEventFilter(
              ScrollEvent.ANY,
              ZoomHandler()
      )
      class Delta {
          var x: Double = 0.toDouble()
          var y: Double = 0.toDouble()
      }
      val dragDelta = Delta()
      onMousePressed = EventHandler { mouseEvent ->
          // record a delta distance for the drag and drop operation.
          cursor = Cursor.MOVE
          dragDelta.x = getLayoutX() - mouseEvent.getSceneX();
          dragDelta.y = getLayoutY() - mouseEvent.getSceneY();
      }
      onMouseReleased = EventHandler { mouseEvent ->
          shapeGroup.translateX += mouseEvent.sceneX + dragDelta.x;
          shapeGroup.translateY += mouseEvent.sceneY + dragDelta.y;
          cursor = Cursor.HAND
      }
      onMouseDragged = EventHandler { mouseEvent ->
          //event pas appelé = pas d'animation
          //TODO
          shapeGroup.translateX += mouseEvent.sceneX + dragDelta.x;
          shapeGroup.translateY += mouseEvent.sceneY + dragDelta.y;
      }
      onMouseEntered = EventHandler {  mouseEvent ->
          cursor = Cursor.HAND
      }
      onMouseClicked = EventHandler {  mouseEvent ->
          if(mouseEvent.clickCount==2){
              shapeGroup.translateX += 400-mouseEvent.sceneX
              shapeGroup.translateY += 400- mouseEvent.sceneY
          }
      }
      setOnKeyPressed { event ->
          println(event.character)
      }
   }

    private inner class ZoomHandler : EventHandler<ScrollEvent> {
        override fun handle(scrollEvent: ScrollEvent) {
            if(scrollEvent.deltaY>0)
                zoomIn()
            else
                zoomOut()
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
      if(shapeGroup.scaleX>0.75&&shapeGroup.scaleY>0.75) {
          shapeGroup.scaleX -= 0.25
          shapeGroup.scaleY -= 0.25
      }
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
        if(idHighlight!=id)
            shapeGroup.children
                    .filter { it.id!=null && it.id.equals(id) }
                    .forEach {
                        it.scaleX = 3.0
                        it.scaleY = 3.0
                        it.style {
                            fill = Color.DARKBLUE
                        }
                    }
        if(idHighlight!=null) {
            //println("lowlight : "+idHighlight)
            shapeGroup.children
                    .filter { it.id!=null && it.id.equals(idHighlight) }
                    .forEach {
                      //colorHighlight= if(isWarehouse) Color.BROWN else Color.GREEN
                        it.scaleX = 1.0
                        it.scaleY = 1.0
                        it.style {
                            fill = colorHighlight
                        }
                    }
        }
        if(idHighlight!=id) {
            idHighlight = id
            colorHighlight = if (isWarehouse) Color.BROWN else Color.GREEN
        }else{
            idHighlight=null
        }
    }

}