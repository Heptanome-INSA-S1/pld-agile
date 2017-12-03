package fr.insalyon.pld.agile.view.fragment

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

  private val colorLine = Color.DARKGREEN
  private val colorWarehouse = Color.INDIANRED
  private val colorDelivery = Color.BLUE
  private val colorCircleHighlight = Color.RED
  private val colorLineHighlight = Color.DARKRED
  private val colorLabel = Color.WHITE
  private val colorLabelHoursHighlight = Color.DARKRED

  private val circleRadius = 10.0

  private val TOTAL_LENGTH : Long by lazy{
    var total = 0L
    round.distancePathInMeters().forEach {
      it.edges.forEach {
        total += it.length
      }
    }
    total
  }

  private val shapeGroup =group {
    val middle = 15.0
    var actualX = 15.0
    var time = round.warehouse.departureHour.toSeconds()
    round.distancePathInMeters().forEach{
      val deplacement = transform(it.edges)
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
    round.distancePathInMeters().forEachIndexed { index,it1 ->
      var deplacement = transform(it1.edges)
      actualX += deplacement
      time += length(it1.edges).seconds.toSeconds()
      val idDelivery = it1.nodes.last().id.toString()
      stackpane {
        id=idDelivery
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
      if(deplacement>30)
        label("${secondsToString(time)}"){
          id=idDelivery
          layoutX=actualX-10.0
          layoutY=middle-23
          style{
            fontSize=10.px
          }
        }
      if(index<round.deliveries().size){
        val attente = round.deliveries().first { it.address.id.toString() == idDelivery }.duration.toSeconds().toDouble()
        time+=attente.toLong()
        if(transform(round.distancePathInMeters().elementAt(index+1).edges)>30)
          label("${secondsToString(time)}"){
            id=it1.nodes.last().id.toString()
            layoutX=actualX-10.0
            layoutY=middle+10
            style{
              fontSize=10.px
            }
          }
      }
    }
    stackpane {
      id=round.warehouse.address.id.toString()
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
    label("${secondsToString(round.warehouse.departureHour.toSeconds())}"){
      id=round.warehouse.address.id.toString()
      layoutX=15.0-10.0
      layoutY=middle+10
      style{
        fontSize=10.px
      }
    }
  }

  override val root: StackPane = stackpane {
    minWidth = parentView.bottom.boundsInLocal.width
    minHeight = parentView.bottom.boundsInLocal.height
    add(shapeGroup)
  }

  private fun length(edges: List<Junction>) : Long {
    var totalLength = 0L
    edges.forEach {
      totalLength += it.length
    }
    return totalLength
  }

  private fun secondsToString(seconds:Long):String{
    var secondes = seconds
    if(secondes==0L)
      return "0s"
    val hours : Long = secondes / 3600L
    secondes -= hours * 3600L
    val minutes : Long = secondes / 60L
    var res = ""
    if(hours != 0L)
      res += ""+ hours + "h"
    if (minutes < 10)
      res += "0"
    res += "" + minutes
    return res
  }

  private fun transform(edges: List<Junction>): Double =
          length(edges).toDouble()/ TOTAL_LENGTH.toDouble() * (parentView.bottom.boundsInLocal.width - 70.0)

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
              .filter { it.id!=null && it.id==idHighlight }
              .forEach {
                if(it is StackPane){
                  val circle = it.getChildList()!!.first() as Circle
                  circle.style {
                    fill = colorHighlight
                  }
                }else if(it is Label){
                  it.style {
                    fontSize=10.px
                    textFill = Color.BLACK
                  }
                }
              }
    }
    if(idHighlight!= idToHighlight) {
      shapeGroup.children
              .filter { it.id != null && it.id ==idToHighlight }
              .forEach {
                if(it is StackPane) {
                  val circle = it.getChildList()!!.first() as Circle
                  circle.style {
                    fill = colorCircleHighlight
                  }
                }else if(it is Label){
                  it.style {
                    fontSize=10.px
                    textFill=colorLabelHoursHighlight
                  }
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