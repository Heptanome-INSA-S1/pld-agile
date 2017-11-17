package fr.insalyon.pld.agile.view

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
  /*// Create an integer property
  val counter = SimpleIntegerProperty()
  // Create a label which will display the value. Match with label fx:id="counterLabel"
  val counterLabel: Label by fxid()*/

  init {
    // Bind the value of label to the property counter
    //counterLabel.bind(counter)
  }

  fun increment() {
    //counter.value += 1
  }
}
