package fr.insalyon.pld.agile.controller.implementation

import com.sun.media.sound.InvalidFormatException
import fr.insalyon.pld.agile.Config.MAP_XSD
import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.getResource
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.serialization.implementation.IntersectionSerializer
import fr.insalyon.pld.agile.util.xml.serialization.implementation.JunctionSerializer
import fr.insalyon.pld.agile.util.xml.serialization.implementation.PlanSerializer
import fr.insalyon.pld.agile.util.xml.validator.implementation.XmlValidatorImpl
import java.io.File
import java.io.FileNotFoundException

open abstract class DefaultState<in T> : State<T> {

  override var plan: Plan? = null
  override var roundRequest: RoundRequest? = null
  override var round: Round? = null

  override fun loadPlan(controller: Controller, pathFile: String) {
    val validator: XmlValidatorImpl = XmlValidatorImpl()
    val sourceFile = File(pathFile)
    val xsdFile = getResource(MAP_XSD)

    if (!sourceFile.exists()) throw FileNotFoundException("The file $pathFile was not found")
    if (sourceFile.extension != "xml") throw InvalidFormatException("The file $pathFile is not a xml file")
    if (!validator.isValid(sourceFile, xsdFile)) throw InvalidFormatException("The file $pathFile does not match the valid pattern")

    val xmlDocument = XmlDocument.open(sourceFile)
    val intersectionSerializer = IntersectionSerializer(xmlDocument)
    val junctionSerializer = JunctionSerializer(xmlDocument)
    val planSerializer = PlanSerializer(xmlDocument, intersectionSerializer, junctionSerializer)

    plan = planSerializer.unserialize(xmlDocument.documentElement)
    controller.changeStateAndInit(controller.LOADED_PLAN_STATE, plan!!)

  }

  override fun loadRoundRequest(controller: Controller, file: File) {}

  override fun calculateRound(controller: Controller) {}

  override fun ok(controller: Controller) {}

  override fun undo(controller: Controller, commands: List<Command>) {}

  override fun redo(controller: Controller, commands: List<Command>) {}

}