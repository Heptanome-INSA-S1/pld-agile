package fr.insalyon.pld.agile

import fr.insalyon.pld.agile.model.km_h

object Config {

  val DEFAULT_MAP = null
  val DEFAULT_DELIVERY = null
  val MAP_XSD = "xsd/map.xsd"
  val DELIVERY_PLANNING_XSD = "xsd/delivery_planning.xsd"

  val defaultSpeed = 15.km_h


  val RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/main/resources/"
  val TEST_RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/test/resources/"
}