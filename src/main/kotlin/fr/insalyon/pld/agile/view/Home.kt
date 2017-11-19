package fr.insalyon.pld.agile.view


import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.serialization.implementation.IntersectionSerializer
import fr.insalyon.pld.agile.util.xml.serialization.implementation.JunctionSerializer
import fr.insalyon.pld.agile.util.xml.serialization.implementation.PlanSerializer
import fr.insalyon.pld.agile.view.fragments.PlanFragment
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import tornadofx.*

/**
 * Default home screen
 */
class Home : View() {
  // Map the current view to resources/view/Home.fxml
    override val root: BorderPane by fxml("/view/Home.fxml")
    // Create an integer property
    val counter = SimpleIntegerProperty()
    // Create a label which will display the value. Match with label fx:id="counterLabel"
    val counterLabel: Label by fxid()
    val plan: Plan

    init {
        // Bind the value of label to the property counter
        counterLabel.bind(counter)


        val xmlPlan = XmlDocument.open("C:/Users/Ordinateur/Documents/INSA/4IF/PLD AGILE/A.G.I.L.E Project/src/main/resources/xml/planLyonGrand.xml")

        val intersectionSerializer = IntersectionSerializer(xmlPlan)
        val junctionSerializer = JunctionSerializer(xmlPlan)

        val planSerializer = PlanSerializer(xmlPlan, intersectionSerializer, junctionSerializer)
        plan = planSerializer.unserialize(xmlPlan.documentElement)

    }

    fun increment() {
        counter.value += 1
        //replaceWith(PlanFragment::class)
    }

    fun planView() {
        replaceWith(find<PlanFragment>(mapOf(PlanFragment::plan to plan)))
    }
}
