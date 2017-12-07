package fr.insalyon.pld.agile

import fr.insalyon.pld.agile.model.h
import fr.insalyon.pld.agile.model.km_h
import fr.insalyon.pld.agile.util.Logger
import javafx.scene.paint.Color
import java.util.*

object Config {

  object Business {
    val DEFAULT_SPEED = 15.km_h
    val DEFAULT_END_DELIVERING = 18 h 0
  }

  object Util {
    val LOGGER_LEVEL = Logger.DEBUG
    val MAP_XSD = "xsd/map.xsd"
    val DELIVERY_PLANNING_XSD = "xsd/delivery_planning.xsd"

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

    val RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/main/resources/"
    val TEST_RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/test/resources/"

  }

  object Errors {

  }


  val prop: Properties = Properties()
  // val inputStream = FileInputStream(getResource("/config/config.properties"))
  //val outputStream = FileOutputStream(getResource("/config/config.properties"))


  /*fun loadLastPlan() {
    prop.load(inputStream)
    DEFAULT_MAP = prop.getProperty("lastFilePath")
  }

  fun updateLastPlan(path: String ) {
    prop.setProperty("lastFilePath",path!!)

    prop.store(outputStream, "")
  }*/
}