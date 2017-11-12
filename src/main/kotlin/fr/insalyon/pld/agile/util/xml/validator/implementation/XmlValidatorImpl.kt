package fr.insalyon.pld.agile.util.xml.validator.implementation

import fr.insalyon.pld.agile.util.xml.validator.api.XmlValidator
import org.w3c.dom.Document
import org.xml.sax.SAXException
import java.io.File
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

class XmlValidatorImpl : XmlValidator {
  override fun isValid(sourceFile: File, xsdFile: File): Boolean {
    val xmlDocument: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(sourceFile)
    val schemaFactory: SchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schema: Schema = schemaFactory.newSchema(xsdFile)

    val validator: Validator = schema.newValidator()

    try {
      validator.validate(DOMSource(xmlDocument))
    } catch (e: SAXException) {
      return false
    }
    return true
  }

}