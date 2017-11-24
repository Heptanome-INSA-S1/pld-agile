package fr.insalyon.pld.agile.controller.implementation

import com.sun.media.sound.InvalidFormatException
import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.Config.MAP_XSD
import fr.insalyon.pld.agile.Config.defaultSpeed
import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.getResource
import fr.insalyon.pld.agile.service.roundcomputing.implementation.RoundComputerImpl
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.serialization.implementation.*
import fr.insalyon.pld.agile.util.xml.validator.implementation.XmlValidatorImpl
import javafx.stage.FileChooser
import java.io.File
import java.io.FileNotFoundException

abstract class DefaultState<in T> : State<T> {

  override fun init(controller: Controller, element: T) {
    println("Etat actuel = DEFAULT_STATE")
    super.init(controller, element)
  }

  protected fun openXmlFileFromDialog(): File? {
    val fileChooser = FileChooser()
    fileChooser.title = "XML Plan"
    fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("XML Files", "*.xml"))
    return fileChooser.showOpenDialog(null)
  }

  override fun loadPlan(controller: Controller, file: File) {
    fileLoadPlanImpl(controller, file)
  }

  override fun loadPlan(controller: Controller) {
    defaultLoadPlanImpl(controller)
  }

  override fun loadRoundRequest(controller: Controller) {
    println("DefaultState action: Load round request was called")
  }

  override fun loadRoundRequest(controller: Controller, file: File) {
    println("DefaultState action: Load round request was called with file")
  }

  override fun calculateRound(controller: Controller) {
    println("DefaultState action: Calculate round was called")
  }

  override fun ok(controller: Controller) {
    println("Ok was called")
  }

  override fun undo(controller: Controller, commands: List<Command>) {}

  override fun redo(controller: Controller, commands: List<Command>) {}

  protected fun defaultLoadPlanImpl(controller: Controller) {
    val validator: XmlValidatorImpl = XmlValidatorImpl()
    val xsdFile = getResource(MAP_XSD)
    val sourceFile = openXmlFileFromDialog() ?: return

    if (!sourceFile.exists()) throw FileNotFoundException("The file ${sourceFile.name} was not found")
    if (sourceFile.extension != "xml") throw InvalidFormatException("The file ${sourceFile.name} is not a xml file")
    if (!validator.isValid(sourceFile, xsdFile)) throw InvalidFormatException("The file ${sourceFile.name} does not match the valid pattern")

    controller.window.loadingPlan()
    val xmlDocument = XmlDocument.open(sourceFile)
    val intersectionSerializer = IntersectionSerializer(xmlDocument)
    val junctionSerializer = JunctionSerializer(xmlDocument)
    val planSerializer = PlanSerializer(xmlDocument, intersectionSerializer, junctionSerializer)

    try {
      val plan = planSerializer.unserialize(xmlDocument.documentElement)
      controller.changeStateAndInit(controller.LOADED_PLAN_STATE, plan)
    } catch (e: Exception) {
      controller.manageException(RuntimeException("Something went wrong during plan parsing"))
    }
  }

  protected fun fileLoadPlanImpl(controller: Controller, sourceFile: File) {
    val validator: XmlValidatorImpl = XmlValidatorImpl()
    val xsdFile = getResource(MAP_XSD)

    if (!sourceFile.exists()) throw FileNotFoundException("The file ${sourceFile.name} was not found")
    if (sourceFile.extension != "xml") throw InvalidFormatException("The file ${sourceFile.name} is not a xml file")
    if (!validator.isValid(sourceFile, xsdFile)) throw InvalidFormatException("The file ${sourceFile.name} does not match the valid pattern")

    controller.window.loadingPlan()

    val xmlDocument = XmlDocument.open(sourceFile)
    val intersectionSerializer = IntersectionSerializer(xmlDocument)
    val junctionSerializer = JunctionSerializer(xmlDocument)
    val planSerializer = PlanSerializer(xmlDocument, intersectionSerializer, junctionSerializer)

    try {
      val plan = planSerializer.unserialize(xmlDocument.documentElement)
      controller.changeStateAndInit(controller.LOADED_PLAN_STATE, plan)
    } catch (e: Exception) {
      controller.manageException(RuntimeException("Something went wrong during plan parsing"))
    }
  }

  protected fun defaultLoadRoundRequestImpl(controller: Controller) {
    val validator: XmlValidatorImpl = XmlValidatorImpl()
    val xsdFile = getResource(Config.DELIVERY_PLANNING_XSD)
    val file = openXmlFileFromDialog() ?: return


    if (file.extension != "xml") throw InvalidFormatException("The file ${file.name} is not a xml file")
    if (!validator.isValid(file, xsdFile)) throw InvalidFormatException("The file ${file.name} does not match the valid pattern")

    controller.window.loadingRound()

    val xmlDocument = XmlDocument.open(file)
    val deliverySerializer = DeliverySerializer(xmlDocument,controller.plan!!)
    val warehouseSerializer = WarehouseSerializer(xmlDocument, controller.plan!!)
    val roundRequestSerializer = RoundRequestSerializer(xmlDocument, deliverySerializer, warehouseSerializer)

    try {
      val roundRequest = roundRequestSerializer.unserialize(xmlDocument.documentElement)
      controller.changeStateAndInit(controller.LOADED_DELIVERIES_STATE, roundRequest)
    } catch (e: Exception) {
      controller.manageException(RuntimeException("Something went wrong during round request parsing"))
    }
  }

  protected fun fileLoadRoundRequestImpl(controller: Controller, file: File){
    val validator: XmlValidatorImpl = XmlValidatorImpl()
    val xsdFile = getResource(Config.DELIVERY_PLANNING_XSD)

    if (file.extension != "xml") throw InvalidFormatException("The file ${file.name} is not a xml file")
    if (!validator.isValid(file, xsdFile)) throw InvalidFormatException("The file ${file.name} does not match the valid pattern")

    controller.window.loadingRound()

    val xmlDocument = XmlDocument.open(file)
    val deliverySerializer = DeliverySerializer(xmlDocument,controller.plan!!)
    val warehouseSerializer = WarehouseSerializer(xmlDocument, controller.plan!!)
    val roundRequestSerializer = RoundRequestSerializer(xmlDocument, deliverySerializer, warehouseSerializer)

    try {
      val roundRequest = roundRequestSerializer.unserialize(xmlDocument.documentElement)
      controller.changeStateAndInit(controller.LOADED_DELIVERIES_STATE, roundRequest)
    } catch (e: Exception) {
      controller.manageException(RuntimeException("Something went wrong during round request parsing"))
    }
  }

  protected fun defaultCalculateRoundImpl(controller: Controller) {
    try {
      val round = RoundComputerImpl(controller.plan!!, controller.roundRequest!!, defaultSpeed).round
      controller.changeStateAndInit(controller.CALCULATED_ROUND_STATE, round)
    } catch (e: Exception) {
      controller.manageException(e)
    }
  }

}