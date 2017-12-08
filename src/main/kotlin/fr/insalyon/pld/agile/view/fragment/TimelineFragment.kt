package fr.insalyon.pld.agile.view.fragment

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.model.Junction
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.seconds
import fr.insalyon.pld.agile.util.Logger
import fr.insalyon.pld.agile.util.min
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
    round.distancePathInMeters().forEachIndexed { index, it ->
      //val deplacement = transform(it.edges)
      val axes = if(index < round.distancePathInMeters().size-1) getAxes(it.edges, index) else Pair(transform(it.edges), 0.0)
      actualX += axes.first

      line {
        id = it.nodes.first().id.toString()
        startX = actualX - axes.first
        startY = middle
        endX = actualX
        endY = middle
        stroke = Config.Util.Colors.colorLine
      }
      if(index < round.getWaitingTimes().size && axes.second > round.getWaitingTimes()[index].toShortFormattedString().length*6){
        label(round.getWaitingTimes()[index].toShortFormattedString()){
          layoutX = actualX + (axes.second/2) - (round.getWaitingTimes()[index].toShortFormattedString().length * 6)
          layoutY = middle + 10
          style {
            fontSize = 10.px
          }
        }

      }
      actualX += axes.second
      line{
        id = it.nodes.first().id.toString()
        startX = actualX - axes.second
        startY = middle
        endX = actualX
        endY = middle
        stroke = Config.Util.Colors.colorLineWait
      }
    }
    actualX = 15.0
    val waitingTimeList = round.getWaitingTimes()
    round.distancePathInMeters().forEachIndexed { index, it1 ->
      val deplacement = transform(it1.edges)
      actualX += deplacement
      time += round.durationPathInSeconds()[index].length.seconds + if (index < waitingTimeList.size) waitingTimeList[index] else 0.seconds
      Logger.debug(time)
      val idDelivery = it1.nodes.last().id.toString()
      stackpane {
        id = idDelivery
        layoutX = actualX - circleRadius
        layoutY = middle - circleRadius
        circle {
          radius = circleRadius
          fill = if (index == round.deliveries().size) Config.Util.Colors.colorWarehouse else Config.Util.Colors.colorDelivery
        }
        label("${index + 2}") {
          style {
            textFill = Config.Util.Colors.colorLabel
          }
        }
      }
      if(deplacement >30)
        label(time.toShortFormattedString()) {
          id = idDelivery
          layoutX = actualX - 10.0
          layoutY = middle - 23
          style {
              fontSize = 10.px
          }
        }
      if (index < round.deliveries().size) {
        time += round.deliveries().first { it.address.id.toString() == idDelivery }.duration
        if(transform(round.distancePathInMeters().elementAt(index+1).edges)>30)
          label(time.toShortFormattedString()) {
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
        fill = Config.Util.Colors.colorWarehouse
        tooltip { "Test " + actualX }
      }
      label("1") {
        style {
          textFill = Config.Util.Colors.colorLabel
        }
      }
    }
    label(round.warehouse.departureHour.toShortFormattedString()) {
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

  private fun getWaitRatio(deliveryIndex: Int): Double{
    val waitTime = round.getWaitingTimes()[deliveryIndex]
    val pathTime = round.durationPathInSeconds()[deliveryIndex]
    return waitTime.toSeconds()*1.0/(waitTime + pathTime).toSeconds()
  }

  private fun getAxes(edges: List<Junction>, deliveryIndex: Int): Pair<Double, Double>{
    val path = transform(edges)
    val waitRatio = getWaitRatio(deliveryIndex)
    return Pair(path * (1-waitRatio), path * waitRatio)
  }

  private fun highlightLocation(idToHighlight: String, isWarehouse: Boolean) {
    if (idHighlight != null) {
      shapeGroup.children
          .filter { it.id != null && it is Line && it.id == idHighlight }
          .forEach {
            (it as Line).stroke = Config.Util.Colors.colorLine
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
                fill = Config.Util.Colors.colorCircleHighlight
              }
            } else if (it is Label) {
              it.style {
                fontSize = 10.px
                textFill = Config.Util.Colors.colorLabelHoursHighlight
              }
            }
          }
      shapeGroup.children
          .filter { it.id != null && it is Line && it.id == idToHighlight }
          .forEach {
            (it as Line).stroke = Config.Util.Colors.colorLineHighlight
            it.strokeWidth = 2.0
          }
    }
    if (idHighlight != idToHighlight) {
      idHighlight = idToHighlight
      colorHighlight = if (isWarehouse) Config.Util.Colors.colorWarehouse else Config.Util.Colors.colorDelivery
    } else {
      idHighlight = null
    }
  }
}