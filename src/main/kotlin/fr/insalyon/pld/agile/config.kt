package fr.insalyon.pld.agile

import fr.insalyon.pld.agile.model.km_h
import fr.insalyon.pld.agile.util.Logger
import javafx.scene.paint.Color
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

object Config {

  val LOGGER_LEVEL = Logger.DEBUG

  var DEFAULT_MAP: String = ""
  val DEFAULT_DELIVERY = null
  val MAP_XSD = "xsd/map.xsd"
  val DELIVERY_PLANNING_XSD = "xsd/delivery_planning.xsd"

  val prop: Properties = Properties()
  val inputStream = FileInputStream(getResource("/config/config.properties"))
  val outputStream = FileOutputStream(getResource("/config/config.properties"))

  val DEFAULT_SPEED = 15.km_h

  val RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/main/resources/"
  val TEST_RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/test/resources/"

  object Colors {
    val colorLine = Color.DARKGREEN
    val colorWarehouse = Color.INDIANRED
    val colorDelivery = Color.BLUE
    val colorCircleHighlight = Color.RED
    val colorLineHighlight = Color.DARKRED
    val colorLabel = Color.WHITE
    val colorLabelHoursHighlight = Color.DARKRED
  }

  fun loadLastPlan() {
    prop.load(inputStream)
    DEFAULT_MAP = prop.getProperty("lastFilePath")
  }

  fun updateLastPlan(path: String ) {
    prop.setProperty("lastFilePath",path!!)

    prop.store(outputStream, "")
  }
}