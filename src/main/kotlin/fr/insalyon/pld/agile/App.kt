package fr.insalyon.pld.agile

import fr.insalyon.pld.agile.model.km_h
import fr.insalyon.pld.agile.service.roundcomputing.implementation.RoundComputerImpl
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.serialization.implementation.*
import fr.insalyon.pld.agile.view.Home


// Create the default start application
class App : tornadofx.App(Home::class)

fun main(args: Array<String>) {

  val xmlPlan = XmlDocument.open(getResource("xml/planLyonPetit.xml"))
  val xmlRoundRequest = XmlDocument.open(getResource("xml/DLpetit3.xml"))

  val planSerializer = PlanSerializer(xmlPlan, IntersectionSerializer(xmlPlan), JunctionSerializer(xmlPlan))
  val plan = planSerializer.unserialize(xmlPlan.documentElement)

  val roundRequestSerializer = RoundRequestSerializer(xmlRoundRequest, DeliverySerializer(xmlRoundRequest,plan), WarehouseSerializer(xmlRoundRequest, plan))
  val roundRequest = roundRequestSerializer.unserialize(xmlRoundRequest.documentElement)

  val round = RoundComputerImpl(plan, roundRequest, 15.km_h).round

  //Application.launch(App::class.java, *args)
}