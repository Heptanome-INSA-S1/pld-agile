package fr.insalyon.pld.agile.controller.implementation

import com.sun.media.sound.InvalidFormatException
import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.getResource
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.validator.implementation.XmlValidatorImpl

class LoadedPlanState : DefaultState<Plan>(), State<Plan> {

  override fun init(element: Plan, windowEvent: Any) {

    println("The plan has been well loaded")
  }

  override fun loadRoundRequest(controller: Controller) {

    val validator: XmlValidatorImpl = XmlValidatorImpl()
    val xsdFile = getResource(Config.DELIVERY_PLANNING_XSD)
    val file = openXmlFileFromDialog() ?: return

    if(file.extension != "xml") throw InvalidFormatException("The file ${file.name} is not a xml file")
    if(!validator.isValid(file, xsdFile)) throw InvalidFormatException("The file ${file.name} does not match the valid pattern")

    val xmlDocument = XmlDocument.open(file)
    // Add code here
    controller.changeStateAndInit(controller.LOADED_DELIVERIES_STATE, RoundRequest(Warehouse(Intersection(10), 8 H 10), setOf<Delivery>()))

  }
}