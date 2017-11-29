package fr.insalyon.pld.agile.view.fragment

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.model.*
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.*

class TimelineFragment() : Fragment() {
  val parentView:BorderPane by param()
  val round : Round by param()
  val planSize: Pair<Double, Double> by param()

  private val colorLine = Color.DARKGREEN
  private val colorWarehouse = Color.INDIANRED
  private val colorDelivery = Color.BLUE

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

  override val root: StackPane = stackpane {
    minWidth = parentView.bottom.boundsInLocal.width
    minHeight = parentView.bottom.boundsInLocal.height
    println(minHeight)

    group {
      paddingLeft=15.0
      val middle = 15.0
      var actualX = 0.0
      round.distancePathInMeters().forEachIndexed { index,it ->
        val deplacement = transform(it.edges)
        timeDeplacement(it.edges)
        actualX += deplacement

        line{
          startX=actualX-deplacement
          startY=middle
          endX=actualX
          endY=middle
          stroke=colorLine
        }
        /*actualX += 100.0
        hlineTo(actualX)*/
        circle {
          centerX = actualX
          centerY = middle
          radius = 5.0
          fill = if(index==round.deliveries().size) colorWarehouse  else colorDelivery
        }
        label("${index+2}."){
          layoutX=actualX
          layoutY=middle
        }
      }
      circle {
        centerX = 0.0
        centerY = middle
        radius = 5.0
        fill = colorWarehouse
        tooltip { "Test " + actualX }
      }
      label("1."){
        layoutX=0.0
        layoutY=middle
      }
    }
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

}