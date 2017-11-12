package fr.insalyon.pld.agile.util.xml.serialization.implementation

import fr.insalyon.pld.agile.model.Intersection
import fr.insalyon.pld.agile.model.Junction
import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.model.XmlConfig
import org.junit.Assert
import org.junit.Test
import org.w3c.dom.Element

import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import javax.xml.parsers.DocumentBuilderFactory

class PlanSerializerTest {

  private val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()

  private val serialiser: LSSerializer by lazy {
    val lsImpl: DOMImplementationLS = document.implementation.getFeature("LS", "3.0") as DOMImplementationLS
    val serializer = lsImpl.createLSSerializer()
    serializer.domConfig.setParameter("xml-declaration", false)
    serializer
  }

  private val intersectionSerializer: IntersectionSerializer = IntersectionSerializer(document)
  private val junctionSerializer: JunctionSerializer = JunctionSerializer(document)
  private val planSerializer: PlanSerializer = PlanSerializer(document, intersectionSerializer, junctionSerializer)

  @Test
  fun serialize() {

    val intersectionA = Intersection(1,0,0)
    val intersectionB = Intersection(2,1,1)
    val intersectionC = Intersection(3,2,2)

    val roadA = Junction(10, "Rue A")
    val roadB = Junction(20, "Rue B")

    val plan: Plan = Plan(
        setOf<Intersection>(
            intersectionA,
            intersectionB,
            intersectionC
        ),
        setOf<Triple<Intersection, Junction, Intersection>>(
            Triple(intersectionA, roadA, intersectionB),
            Triple(intersectionB, roadB, intersectionC)
        )
    )

    val planAsElement: Element = planSerializer.serialize(plan)

    val stringBuilder: StringBuilder = StringBuilder()
        .append("<reseau>")
        .append("<noeud id=\"1\" x=\"0\" y=\"0\"/>")
        .append("<noeud id=\"2\" x=\"1\" y=\"1\"/>")
        .append("<noeud id=\"3\" x=\"2\" y=\"2\"/>")
        .append("<troncon destination=\"2\" longueur=\"10.0\" nomRue=\"Rue A\" origine=\"1\"/>")
        .append("<troncon destination=\"3\" longueur=\"20.0\" nomRue=\"Rue B\" origine=\"2\"/>")
        .append("</reseau>")
    val planAsString = stringBuilder.toString()
    Assert.assertEquals(planAsString, serialiser.writeToString(planAsElement))

  }

  @Test
  fun unserialize() {

    val planAsElement = document.createElement(XmlConfig.Map.TAG)

    val intersection1AsElement = document.createElement(XmlConfig.Intersection.TAG)
    intersection1AsElement.setAttribute(XmlConfig.Intersection.ID, "1")
    intersection1AsElement.setAttribute(XmlConfig.Intersection.X, "2")
    intersection1AsElement.setAttribute(XmlConfig.Intersection.Y, "3")

    val intersection2AsElement = document.createElement(XmlConfig.Intersection.TAG)
    intersection2AsElement.setAttribute(XmlConfig.Intersection.ID, "2")
    intersection2AsElement.setAttribute(XmlConfig.Intersection.X, "4")
    intersection2AsElement.setAttribute(XmlConfig.Intersection.Y, "5")

    val roadAsElement = document.createElement(XmlConfig.Junction.TAG)
    roadAsElement.setAttribute(XmlConfig.Junction.FROM, "1")
    roadAsElement.setAttribute(XmlConfig.Junction.TO, "2")
    roadAsElement.setAttribute(XmlConfig.Junction.LENGTH, "20.0")
    roadAsElement.setAttribute(XmlConfig.Junction.NAME, "Main road")

    planAsElement.appendChild(intersection1AsElement)
    planAsElement.appendChild(intersection2AsElement)
    planAsElement.appendChild(roadAsElement)

    val plan: Plan = planSerializer.unserialize(planAsElement)

    Assert.assertEquals(2, plan.nodes.size)
    Assert.assertEquals(1, plan.outEdges[plan.nodes.first { it.element.id == 1 }.id].size)
    Assert.assertEquals(0, plan.outEdges[plan.nodes.first { it.element.id == 2 }.id].size)
    Assert.assertEquals(0, plan.inEdges[plan.nodes.first { it.element.id == 1 }.id].size)
    Assert.assertEquals(1, plan.inEdges[plan.nodes.first { it.element.id == 2 }.id].size)
    val road = plan.outEdges[plan.nodes.first { it.element.id == 1 }.id].first()

    Assert.assertEquals("Main road", road.element.name)
    Assert.assertEquals(20, road.element.length)

    Assert.assertEquals(1, road.from.element.id)
    Assert.assertEquals(2, road.to.element.id)

  }

}