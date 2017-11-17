package fr.insalyon.pld.agile.controller.implementation

import com.sun.media.sound.InvalidFormatException
import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.api.State
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.serialization.implementation.IntersectionSerializer
import fr.insalyon.pld.agile.util.xml.serialization.implementation.JunctionSerializer
import fr.insalyon.pld.agile.util.xml.serialization.implementation.PlanSerializer
import fr.insalyon.pld.agile.util.xml.validator.implementation.XmlValidatorImpl
import java.io.File
import java.io.FileNotFoundException

class DefaultState : State {
    override fun loadPlan(pathFile: String): Plan {
        val validator: XmlValidatorImpl = XmlValidatorImpl()
        val sourceFile = File(pathFile)
        val xsdFile = File("./../../../resources/xsd/map.xsd")

        if(!sourceFile.exists()) throw FileNotFoundException("The file $pathFile was not found")
        if(sourceFile.extension != "xml") throw InvalidFormatException("The file $pathFile is not a xml file")
        if(!validator.isValid(sourceFile, xsdFile)) throw InvalidFormatException("The file $pathFile does not match the valid pattern")

        val xmlDocument = XmlDocument.open(sourceFile)
        val intersectionSerializer = IntersectionSerializer(xmlDocument)
        val junctionSerializer = JunctionSerializer(xmlDocument)
        val planSerializer = PlanSerializer(xmlDocument, intersectionSerializer, junctionSerializer)

        return planSerializer.unserialize(xmlDocument.documentElement)

    }

    override fun loadRoundRequest(file: File): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun calculateRound(): Round {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun ok(state: State): State {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun undo(commands: List<Command>): List<Command> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun redo(commands: List<Command>): List<Command> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}