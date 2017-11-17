package fr.insalyon.pld.agile.util.xml

import org.w3c.dom.Document
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class XmlDocument {

  companion object {
    fun open(path: String): Document {
      val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
      val builder: DocumentBuilder = factory.newDocumentBuilder()

      return builder.parse(File(path))
    }
  }

}