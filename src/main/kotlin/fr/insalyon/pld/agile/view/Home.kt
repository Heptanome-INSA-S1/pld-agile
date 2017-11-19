package fr.insalyon.pld.agile.view


import fr.insalyon.pld.agile.model.Plan
import fr.insalyon.pld.agile.util.xml.XmlDocument
import fr.insalyon.pld.agile.util.xml.serialization.implementation.IntersectionSerializer
import fr.insalyon.pld.agile.util.xml.serialization.implementation.JunctionSerializer
import fr.insalyon.pld.agile.util.xml.serialization.implementation.PlanSerializer
import fr.insalyon.pld.agile.view.fragments.PlanFragment
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import tornadofx.*

/**
 * Default home screen
 */
class Home : View() {
    override val root: BorderPane by fxml("/view/Home.fxml")
    // Map the current view to resources/view/Home.fxml
    val plan: Plan
    val loadPlanButton: Button by fxid();
    val centerVBox: VBox by fxid();

    init {

        val xmlPlan = XmlDocument.open("src/main/resources/xml/planLyonGrand.xml")
        val intersectionSerializer = IntersectionSerializer(xmlPlan)
        val junctionSerializer = JunctionSerializer(xmlPlan)
        val planSerializer = PlanSerializer(xmlPlan, intersectionSerializer, junctionSerializer)
        plan = planSerializer.unserialize(xmlPlan.documentElement)

        loadPlanButton.setOnAction {
            planView()
        }
    }
    fun planView() {
        root.center{
            add(PlanFragment::class, mapOf(PlanFragment::plan to plan))
            //replaceWith(find<PlanFragment>(PlanFragment::class, plan))
            //centerVBox.replaceWith(find<PlanFragment>(mapOf(PlanFragment::plan to plan)))
        }
    }
}
