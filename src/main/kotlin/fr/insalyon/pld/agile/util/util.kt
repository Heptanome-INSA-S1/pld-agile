package fr.insalyon.pld.agile.util

import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.RoundRequest
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.serialization.implementation.*
import java.io.File

@JvmName("maxNotNull")
fun <E : Comparable<E>> max(a: E, b: E): E {
  return if(a > b) { a } else { b }
}

@JvmName("maxNullFirst")
fun <E : Comparable<E>> max(a: E?, b: E): E {
  return if(a!= null && a > b) { a } else { b }
}

@JvmName("maxNullSecond")
fun <E : Comparable<E>> max(a: E, b: E?): E {
  return if(b == null || a > b) { a } else { b }
}

@JvmName("minNotNull")
fun <E : Comparable<E>> min(a: E, b: E): E {
  return if(a < b) { a } else { b }
}

@JvmName("minNullFirst")
fun <E : Comparable<E>> min(a: E?, b: E): E {
  return if(a!= null && a < b) { a } else { b }
}

@JvmName("minNullSecond")
fun <E : Comparable<E>> min(a: E, b: E?): E {
  return if(b == null || a < b) { a } else { b }
}

fun <E> Iterable<E>.toLinkedHashSet(): LinkedHashSet<E> {
  val linkedHashSet = LinkedHashSet<E>()
  linkedHashSet += this
  return linkedHashSet
}

fun File.toPlan(): Plan {
  val xmlDocument = XmlDocument.open(this)
  val intersectionSerializer = IntersectionSerializer(xmlDocument)
  val junctionSerializer = JunctionSerializer(xmlDocument)
  val planSerializer = PlanSerializer(xmlDocument, intersectionSerializer, junctionSerializer)

  return planSerializer.unserialize(xmlDocument.documentElement)
}

fun File.toRoundRequest(plan: Plan): RoundRequest {
  val xmlDocument = XmlDocument.open(this)
  val deliverySerializer = DeliverySerializer(xmlDocument, plan)
  val warehouseSerializer = WarehouseSerializer(xmlDocument, plan)
  val roundRequestSerializer = RoundRequestSerializer(xmlDocument, deliverySerializer, warehouseSerializer)

  return roundRequestSerializer.unserialize(xmlDocument.documentElement)
}