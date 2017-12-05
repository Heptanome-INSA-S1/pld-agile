package fr.insalyon.pld.agile.view.fragment

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.view.event.HighlightLocationEvent
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import tornadofx.*

class TimelineFragment: Fragment() {
  val parentView:BorderPane by param()
  val round : Round by param()
  val planSize: Pair<Double, Double> by param()

  var idHighlight: String? = null
  var colorHighlight: Color = Color.WHITE

  private val circleRadius = 10.0

  private val TOTAL_LENGTH: Int by lazy {
    round.distance
  }

  private val shapeGroup = group {
    val middle = 15.0
    var actualX = 15.0
    var time = round.warehouse.departureHour
    round.distancePathInMeters().forEach {
      val deplacement = transform(it.edges)
      actualX += deplacement

      line {
        id = it.nodes.first().id.toString()
        startX = actualX - deplacement
        startY = middle
        endX = actualX
        endY = middle
        stroke = Config.Colors.colorLine
      }
    }
    actualX = 15.0
    round.distancePathInMeters().forEachIndexed { index, it1 ->
      val deplacement = transform(it1.edges)
      actualX += deplacement
      time += length(it1.edges).seconds
      val idDelivery = it1.nodes.last().id.toString()
      stackpane {
        id = idDelivery
        layoutX = actualX - circleRadius
        layoutY = middle - circleRadius
        circle {
          radius = circleRadius
          fill = if (index == round.deliveries().size) Config.Colors.colorWarehouse else Config.Colors.colorDelivery
        }
        label("${index + 2}") {
          style {
            textFill = Config.Colors.colorLabel
          }
        }
      }
      if(deplacement>30)
        label(time.toFormattedString()) {
          id = idDelivery
          layoutX = actualX - 10.0
          layoutY = middle - 23
          style {
            fontSize = 10.px
        }
      }
      if (index < round.deliveries().size) {
        time += round.deliveries().first { it.address.id.toString() == idDelivery }.duration
        label(time.toFormattedString()) {
          id = it1.nodes.last().id.toString()
          layoutX = actualX - 10.0
          layoutY = middle + 10
          style {
            fontSize = 10.px
          }
        }
      }
    }
    stackpane {
      id = round.warehouse.address.id.toString()
      layoutX = 15.0 - circleRadius
      layoutY = middle - circleRadius
      circle {
        radius = circleRadius
        fill = Config.Colors.colorWarehouse
        tooltip { "Test " + actualX }
      }
      label("1") {
        style {
          textFill = Config.Colors.colorLabel
        }
      }
    }
    label(round.warehouse.departureHour.toFormattedString()) {
      id = round.warehouse.address.id.toString()
      layoutX = 15.0 - 10.0
      layoutY = middle + 10
      style {
        fontSize = 10.px
      }
    }
  }

  override val root: StackPane = stackpane {
    minWidth = parentView.bottom.boundsInLocal.width
    minHeight = parentView.bottom.boundsInLocal.height
    add(shapeGroup)
  }

  init {
    subscribe<HighlightLocationEvent> { event ->
      highlightLocation(event.id, event.isWarehouse)
    }
  }
  
  private fun secondsToString(seconds:Int):String{
    var secondes = seconds
    if(secondes==0)
      return "0s"
    val hours : Int = secondes / 3600
    secondes -= hours * 3600
    val minutes : Int = secondes / 60
    var res = ""
    if(hours != 0)
      res += ""+ hours + "h"
    if (minutes < 10)
      res += "0"
    res += "" + minutes
    return res
  }

  private fun length(edges: List<Junction>): Int = edges.sumBy { it.length }

  private fun transform(edges: List<Junction>): Double =
      length(edges).toDouble() / TOTAL_LENGTH.toDouble() * (parentView.bottom.boundsInLocal.width - 70.0)

  private fun highlightLocation(idToHighlight: String, isWarehouse: Boolean) {
    if (idHighlight != null) {
      shapeGroup.children
          .filter { it.id != null && it is Line && it.id == idHighlight }
          .forEach {
            (it as Line).stroke = Config.Colors.colorLine
            it.strokeWidth = 1.0
          }
      shapeGroup.children
          .filter { it.id != null && it.id == idHighlight }
          .forEach {
            if (it is StackPane) {
              val circle = it.getChildList()!!.first() as Circle
              circle.style {
                fill = colorHighlight
              }
            } else if (it is Label) {
              it.style {
                fontSize = 10.px
                textFill = Color.BLACK
              }
            }
          }
    }
    if (idHighlight != idToHighlight) {
      shapeGroup.children
          .filter { it.id != null && it.id == idToHighlight }
          .forEach {
            if (it is StackPane) {
              val circle = it.getChildList()!!.first() as Circle
              circle.style {
                fill = Config.Colors.colorCircleHighlight
              }
            } else if (it is Label) {
              it.style {
                fontSize = 10.px
                textFill = Config.Colors.colorLabelHoursHighlight
              }
            }
          }
      shapeGroup.children
          .filter { it.id != null && it is Line && it.id == idToHighlight }
          .forEach {
            (it as Line).stroke = Config.Colors.colorLineHighlight
            it.strokeWidth = 2.0
          }
    }
    if (idHighlight != idToHighlight) {
      idHighlight = idToHighlight
      colorHighlight = if (isWarehouse) Config.Colors.colorWarehouse else Config.Colors.colorDelivery
    } else {
      idHighlight = null
    }
  }
}