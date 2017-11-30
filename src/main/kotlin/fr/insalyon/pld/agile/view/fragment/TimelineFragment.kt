package fr.insalyon.pld.agile.view.fragment

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.view.event.HighlightLocationEvent
import javafx.scene.Group
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import tornadofx.*

class TimelineFragment() : Fragment() {
  val parentView:BorderPane by param()
  val round : Round by param()
  val planSize: Pair<Double, Double> by param()

  private val colorLine = Color.DARKGREEN
  private val colorWarehouse = Color.INDIANRED
  private val colorDelivery = Color.BLUE
  private val colorCircleHighlight = Color.RED
  private val colorLineHighlight = Color.DARKRED
  private val colorLabel = Color.WHITE

  private val circleRadius = 10.0

  val TOTAL_LENGTH : Long by lazy{
    var total = 0L
    round.distancePathInMeters().forEach {
      it.edges.forEach {
        total += it.length
      }
    }
    total
  }

  val TOTAL_DURATION : Long by lazy{
    var total = 0L
    round.durationPathInSeconds().forEach {
      it.edges.forEach {
        total += it.length
      }
    }
    total
  }
  val shapeGroup =group {
    val middle = 15.0
    var actualX = 15.0
    round.distancePathInMeters().forEachIndexed { index, it ->
      val deplacement = transform(it.edges)
      timeDeplacement(it.edges)
      actualX += deplacement

      line {
        id = it.nodes.first().id.toString()
        startX = actualX - deplacement
        startY = middle
        endX = actualX
        endY = middle
        stroke = colorLine
      }
    }
    actualX=15.0
    round.distancePathInMeters().forEachIndexed { index,it ->
      val deplacement = transform(it.edges)
      timeDeplacement(it.edges)
      actualX += deplacement

      stackpane {
        id=it.nodes.last().id.toString()
        layoutX=actualX - circleRadius
        layoutY=middle - circleRadius
        circle {
          radius = circleRadius
          fill = if(index==round.deliveries().size) colorWarehouse  else colorDelivery
        }
        label("${index+2}"){
          style{
            textFill=colorLabel
          }
        }
      }
    }
    stackpane {
      layoutX=15.0 - circleRadius
      layoutY=middle - circleRadius
      circle {
        radius = circleRadius
        fill = colorWarehouse
        tooltip { "Test " + actualX }
      }
      label("1"){
        style{
          textFill=colorLabel
        }
      }
    }
  }
  override val root: StackPane = stackpane {
    minWidth = parentView.bottom.boundsInLocal.width
    minHeight = parentView.bottom.boundsInLocal.height
    println(minHeight)
    add(shapeGroup)

  }

  private fun length(edges: List<Junction>) : Long {
    var totalLength = 0L
    edges.forEach {
      totalLength += it.length
    }
    return totalLength
  }

  private fun timeDeplacement(edges: List<Junction>){


    val duration : Duration = length(edges).seconds


    println(duration)
  }

  private fun transform(edges: List<Junction>): Double {
    return length(edges).toDouble()/ TOTAL_LENGTH.toDouble() * (parentView.bottom.boundsInLocal.width - 70.0)
  }

  init {
    subscribe<HighlightLocationEvent> {
      event -> highlightLocation(event.id,event.isWarehouse)
    }
  }

  var idHighlight: String? =null
  var colorHighlight:Color =Color.WHITE

  private fun highlightLocation(idToHighlight:String, isWarehouse:Boolean){
    if(idHighlight!=null) {
      shapeGroup.children
              .filter { it.id != null && it is Line && it.id ==idHighlight }
              .forEach {
                (it as Line).stroke = colorLine
                it.strokeWidth = 1.0
              }
      shapeGroup.children
              .filter { it.id!=null && it is StackPane && it.id==idHighlight }
              .forEach {
                val circle = it.getChildList()!!.first() as Circle
                //it.scaleX = 1.0
                //it.scaleY = 1.0
                circle.style {
                  fill = colorHighlight
                }
              }
    }
    if(idHighlight!= idToHighlight) {
      shapeGroup.children
              .filter { it.id != null && it is StackPane && it.id ==idToHighlight }
              .forEach {
                val circle = it.getChildList()!!.first() as Circle
                //it.scaleX = 1.5
                //it.scaleY = 1.5
                circle.style {
                  fill = colorCircleHighlight
                }
              }
      shapeGroup.children
              .filter { it.id != null && it is Line && it.id ==idToHighlight }
              .forEach {
                (it as Line).stroke = colorLineHighlight
                it.strokeWidth = 2.0
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