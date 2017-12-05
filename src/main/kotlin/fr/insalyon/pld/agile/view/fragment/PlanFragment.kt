package fr.insalyon.pld.agile.view.fragment

import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.util.Logger
import fr.insalyon.pld.agile.view.event.HighlightLocationEvent
import fr.insalyon.pld.agile.view.event.HighlightLocationInListEvent
import javafx.animation.TranslateTransition
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.control.ScrollPane
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.util.Duration
import tornadofx.*
import java.lang.Math.*


const val UP : Int = 0
const val RIGHT : Int = 1
const val DOWN : Int = 2
const val LEFT : Int = 3
const val CENTER : Int = 4

class PlanFragment : Fragment(){

    private val colorLine :Color = Color.ORANGE
    private val colorDelivery:Color = Color.GREEN
    private val colorWarehouse:Color = Color.BROWN
    private val colorLabelCircle :Color = Color.LIGHTGREEN
    private val colorLineHighlight :Color = Color.RED
    private val colorCircleHighlight :Color = Color.DARKBLUE

  val parentView: BorderPane by param()
  val plan: Plan by param()
  val round: Round? by param()

  var MAP_SIZE=min(parentView.center.boundsInLocal.width,parentView.center.boundsInLocal.height)

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

      notNullRound.distancePathInMeters().forEach{
        var fromX: Double
        var fromY: Double
        var toX = 0.0
        var toY = 0.0
        var index = 0
          group{
              id=it.nodes.first().id.toString()
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
                          stroke = colorLine
                      }
                  } else {
                      toX = nodeX
                      toY = nodeY
                  }
                  index++
              }
          }
      }

      val (warehouseXPos, warehouseYPos) = transform(notNullRound.warehouse.address.x, notNullRound.warehouse.address.y)
      circle {
        centerX = warehouseXPos
        centerY = warehouseYPos
        radius = SIZE * 7
        fill = colorWarehouse
        id = notNullRound.warehouse.address.id.toString()
        onHover { fire(HighlightLocationInListEvent(id,Color.LIGHTCORAL)) }
        setOnMouseExited { fire(HighlightLocationInListEvent(id,Color.WHITE)) }
      }
      notNullRound.deliveries().forEachIndexed { index, it ->
        val (nodeX, nodeY) = transform(it.address.x, it.address.y)
          stackpane {
              id = it.address.id.toString()
              layoutX=nodeX-SIZE*7
              layoutY=nodeY-SIZE*7
              onHover { fire(HighlightLocationInListEvent(id,Color.LIGHTGREEN)) }
              setOnMouseExited { fire(HighlightLocationInListEvent(id,Color.WHITE)) }
              circle {
                  radius = SIZE * 7
                  fill = colorDelivery
              }

              label(""+(index+2)){
                  style{
                      fontSize=7. px
                      textFill=colorLabelCircle
                  }
              }
          }
      }
    }
  }

  val scroll = scrollpane {
      shapeGroup.translateX=(parentView.center.boundsInLocal.width-MAP_SIZE)/2
      println((parentView.center.boundsInLocal.width-MAP_SIZE)/2)
      shapeGroup.translateY=(parentView.center.boundsInLocal.height-MAP_SIZE)/2
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
      onDragDetected=EventHandler {
          Logger.debug("onDragDetected")
          startFullDrag()
      }
      onMousePressed = EventHandler { mouseEvent ->
          // record a delta distance for the drag and drop operation.
          cursor = Cursor.MOVE
          //println(""+shapeGroup.translateX +" "+mouseEvent.sceneX+" "+ dragDelta.x)
          //println(""+parentView.center.boundsInLocal.width+" "+layoutX+" "+mouseEvent.sceneX)
          dragDelta.x = (shapeGroup.translateX - mouseEvent.sceneX)
          dragDelta.y = (shapeGroup.translateY - mouseEvent.sceneY)
      }
      onMouseReleased = EventHandler {
          cursor = Cursor.HAND
      }
      onMouseDragOver=EventHandler { mouseEvent ->
          //println(""+ mouseEvent.sceneX +" "+ dragDelta.x+" "+shapeGroup.scaleX+" "+(parentView.center.boundsInLocal.width/2)*(1-shapeGroup.scaleX)+" "+parentView.center.boundsInLocal.width)
          //println(""+abs(mouseEvent.sceneX + dragDelta.x)*MAP_SIZE/pow(parentView.center.boundsInLocal.width,2.0)+" "+abs(shapeGroup.scaleX))
          if(abs(mouseEvent.sceneX + dragDelta.x-(parentView.center.boundsInLocal.width-MAP_SIZE)/2)*parentView.center.boundsInLocal.width/ pow(MAP_SIZE,2.0)*2 <abs(shapeGroup.scaleX)||abs(mouseEvent.sceneX + dragDelta.x)<abs(shapeGroup.translateX))
            shapeGroup.translateX = mouseEvent.sceneX + dragDelta.x
          if(abs(mouseEvent.sceneY + dragDelta.y-(parentView.center.boundsInLocal.height-MAP_SIZE)/2)*parentView.center.boundsInLocal.height/ pow(MAP_SIZE,2.0)*2 <abs(shapeGroup.scaleY)||abs(mouseEvent.sceneY + dragDelta.y)<abs(shapeGroup.translateY))
            shapeGroup.translateY = mouseEvent.sceneY + dragDelta.y
      }
      onMouseClicked = EventHandler { mouseEvent ->
          if (mouseEvent.clickCount == 2) {
              val tt = TranslateTransition(Duration.millis(500.0), shapeGroup)
              if(abs(shapeGroup.translateX +(MAP_SIZE/2) - mouseEvent.sceneX)<(MAP_SIZE/2)*abs(1-shapeGroup.scaleX)+50 )
                  tt.toX =  shapeGroup.translateX + (MAP_SIZE/2) - mouseEvent.sceneX
              else
                  tt.toX = ((MAP_SIZE/2)*abs(1-shapeGroup.scaleX)+50)*((MAP_SIZE/2)-mouseEvent.sceneX)/abs((MAP_SIZE/2)-mouseEvent.sceneX) //TODO chercher comment avoir le signe
              if(abs(shapeGroup.translateY +(MAP_SIZE/2) - mouseEvent.sceneY)<(MAP_SIZE/2)*abs(1-shapeGroup.scaleY)+50)
                  tt.toY = shapeGroup.translateY + (MAP_SIZE/2) - mouseEvent.sceneY
              else
                  tt.toY = ((MAP_SIZE/2)*abs(1-shapeGroup.scaleY)+50)*((MAP_SIZE/2)-mouseEvent.sceneY)/abs((MAP_SIZE/2)-mouseEvent.sceneY)
              tt.play()
          }
      }
      setOnKeyPressed { event ->
          Logger.debug(event.character)
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

  override val root = scroll


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
  }

    init {
        subscribe<HighlightLocationEvent> {
            event -> highlightLocation(event.id,event.isWarehouse)
        }
    }


    var idHighlight: String? =null
    var colorHighlight:Color =Color.GREEN

    private fun highlightLocation(idToHighlight:String, isWarehouse:Boolean){
        if(idHighlight!=null) {
            shapeGroup.children
                    .filter { it.id!=null && it is StackPane && it.id==idHighlight }
                    .forEach {
                        var circle:Circle = it.getChildList()!!.first() as Circle
                        circle.scaleX = 1.0
                        circle.scaleY = 1.0
                        circle.style {
                            fill = colorHighlight
                        }
                    }
            shapeGroup.children
                    .filter { it.id != null && it is Group && it.id ==idHighlight }
                    .forEach {
                        it.getChildList()!!.forEach {
                            (it as Line).stroke = colorLine
                        }
                    }
        }
        Logger.debug("highlight : "+ idToHighlight)
        if(idHighlight!= idToHighlight) {
            shapeGroup.children
                    .filter { it.id != null && it is StackPane && it.id ==idToHighlight }
                    .forEach {
                        var circle:Circle = it.getChildList()!!.first() as Circle
                        circle.scaleX = 1.5
                        circle.scaleY = 1.5
                        circle.style {
                            fill = colorCircleHighlight
                        }
                    }
            shapeGroup.children
                    .filter { it.id != null && it is Group && it.id ==idToHighlight }
                    .forEach {
                        it.getChildList()!!.forEach {
                            (it as Line).stroke = colorLineHighlight
                        }
                    }
        }
        if(idHighlight!= idToHighlight) {
            idHighlight = idToHighlight
            colorHighlight = if (isWarehouse) colorWarehouse else colorDelivery
        }else{
            idHighlight=null
        }
    }

}