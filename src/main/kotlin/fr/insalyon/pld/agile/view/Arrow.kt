package fr.insalyon.pld.agile.view

import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import tornadofx.*

class Arrow @JvmOverloads constructor(startX: Double, startY: Double, endX: Double, endY: Double, arrowHeadSize: Double = defaultArrowHeadSize, color: Color = Color.BLACK) : Path() {

  init {
    strokeProperty().bind(fillProperty())
    fill = color

    //Line
    elements.add(MoveTo(startX, startY))
    elements.add(LineTo(endX, endY))

    //ArrowHead
    val angle = Math.atan2(endY - startY, endX - startX) - Math.PI / 2.0
    val sin = Math.sin(angle)
    val cos = Math.cos(angle)
    //point1
    val x1 = (-1.0 / 2.0 * cos + Math.sqrt(3.0) / 2 * sin) * arrowHeadSize + endX
    val y1 = (-1.0 / 2.0 * sin - Math.sqrt(3.0) / 2 * cos) * arrowHeadSize + endY
    //point2
    val x2 = (1.0 / 2.0 * cos + Math.sqrt(3.0) / 2 * sin) * arrowHeadSize + endX
    val y2 = (1.0 / 2.0 * sin - Math.sqrt(3.0) / 2 * cos) * arrowHeadSize + endY

    elements.add(LineTo(x1, y1))
    elements.add(LineTo(x2, y2))
    elements.add(LineTo(endX, endY))
  }

  companion object {
    private val defaultArrowHeadSize = 7.0
  }
}

fun Parent.arrow(startX: Number = 0, startY: Number = 0, endX: Number = 0, endY: Number = 0, headSize: Number = 5.0, color: Color = Color.BLACK, op: (Arrow.() -> Unit)? = null) =
    opcr(this, Arrow(startX.toDouble(), startY.toDouble(), endX.toDouble(), endY.toDouble(), headSize.toDouble(), color), op)