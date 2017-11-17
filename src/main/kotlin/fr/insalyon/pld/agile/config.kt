package fr.insalyon.pld.agile

object Config {

  val DEFAULT_MAP = null
  val DEFAULT_DELIVERY = null
  val MAP_XSD = "xsd/map.xsd"
  val DELIVERY_PLANNING_XSD = "xsd/delivery_planning.xsd"



  val RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/main/resources/"
  val TEST_RESOURCE_FOLDER = System.getProperty("user.dir").replace("\\", "/") + "/src/test/resources/"
}