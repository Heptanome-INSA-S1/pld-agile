package fr.insalyon.pld.agile.view.fragment


import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.view.Home
import javafx.scene.layout.BorderPane
import tornadofx.*

class DeliveryAdd: Fragment("Ajouter une livraison"){

  val intersection : Intersection by param()
  val parentView: Home by param()

  val startTime = textfield{
    promptText = "H:M:S"
  }
  val endTime = textfield{
    promptText = "H:M:S"
  }
  val hourDuration = textfield{
    promptText = "H"
  }
  val minDuration = textfield{
    promptText = "M"
  }
  val that = this
  override val root: Form = form {
    fieldset("Créneau de livraison :") {
      field("Debut :") {
        add(startTime)
      }
      field("Fin :") {
        add(endTime)
      }
    }

    fieldset {

      hbox(10) {

        field("Durée de livraison :"){
          add(hourDuration)
          label("h")
        }
        field{
          add(minDuration)
          label(" min")
        }
      }
    }

    borderpane{
      left {
        button("Créer") {
          action {
            parentView.controller.saveNewDelivery(
                that,
                Delivery(
                    intersection,
                    startTime.text.toInstant(),
                    endTime.text.toInstant(),
                    Duration(
                        if(hourDuration.text != "") hourDuration.text.toInt() else 0,
                        if(minDuration.text != "" ) minDuration.text.toInt() else 0
                    )
                )
            )
          }
        }
      }
      right{
        button("Annuler") {
          action {
            parentView.controller.closeDeliveryAdd(that)
          }
        }
      }
    }
  }
}