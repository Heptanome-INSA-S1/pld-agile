package fr.insalyon.pld.agile

import fr.insalyon.pld.agile.model.km_h
import fr.insalyon.pld.agile.util.Logger
import javafx.scene.paint.Color

object Config {

  val LOGGER_LEVEL = Logger.DEBUG

  val DEFAULT_MAP = null
  val DEFAULT_DELIVERY = null
  val MAP_XSD = "xsd/map.xsd"
  val DELIVERY_PLANNING_XSD = "xsd/delivery_planning.xsd"

  val DEFAULT_SPEED = 15.km_h

  val RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/main/resources/"
  val TEST_RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/test/resources/"

  val WINDOW_IS_MAX_SIZE = false

  object Colors {
    val colorLine = Color.DARKGREEN
    val colorWarehouse = Color.INDIANRED
    val colorDelivery = Color.BLUE
    val colorCircleHighlight = Color.RED
    val colorLineHighlight = Color.DARKRED
    val colorLabel = Color.WHITE
    val colorLabelHoursHighlight = Color.DARKRED
  }

}