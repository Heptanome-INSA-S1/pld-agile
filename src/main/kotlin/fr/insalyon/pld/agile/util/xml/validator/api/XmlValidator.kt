package fr.insalyon.pld.agile.util.xml.validator.api

import java.io.File

/**
 * Xml validator working with a xsd file
 */
interface XmlValidator {

  /**
   * @param sourceFile: The xml file to check
   * @param xsdFile: The xsd schema
   * @return if the source file matches to the xsd specifications
   */
  fun isValid(sourceFile: File, xsdFile: File): Boolean

}