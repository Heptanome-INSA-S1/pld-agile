package fr.insalyon.pld.agile.util.xml.serialization.api

import org.w3c.dom.Element

/**
 * XmlSerializer for `E` objects
 */
interface XmlSerializer<E> {

  /**
   * Serialize the element to an xml Element
   */
  fun serialize(element: E): Element

  /**
   * Unserialize a xml Element to an object of type `E`
   */
  fun unserialize(element: Element): E

}