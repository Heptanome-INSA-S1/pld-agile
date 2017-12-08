package fr.insalyon.pld.agile

import fr.insalyon.pld.agile.model.h
import fr.insalyon.pld.agile.model.km_h
import fr.insalyon.pld.agile.util.Logger
import javafx.scene.paint.Color
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*
import java.io.File



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
      val colorLine = Color.DARKGREEN!!
      val colorWarehouse = Color.INDIANRED!!
      val colorDelivery = Color.BLUE!!
      val colorCircleHighlight = Color.RED!!
      val colorLineHighlight = Color.DARKRED!!
      val colorLabel = Color.WHITE!!
      val colorLabelHoursHighlight = Color.DARKRED!!
    }

    val RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/main/resources/"
    val TEST_RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/test/resources/"

  }

  private val prop: Properties = Properties()
  private val configFile :String = "config.properties"


  fun loadLastPlan() : String? {
    return try {
      var inputStream = FileInputStream(getResource(configFile))
      prop.load(inputStream)
      prop.getProperty("lastFilePath")
    }catch (e: FileNotFoundException){
      null
    }
  }

  fun updateLastPlan(path: String ) {
    val file = File(configFile)
    file.createNewFile()
    val outputStream = FileOutputStream(getResource(configFile))
    prop.setProperty("lastFilePath",path)

    prop.store(outputStream, "")
  }
}