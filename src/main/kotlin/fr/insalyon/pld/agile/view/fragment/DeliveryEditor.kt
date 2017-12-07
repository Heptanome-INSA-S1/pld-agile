package fr.insalyon.pld.agile.view.fragment


import com.sun.javaws.exceptions.InvalidArgumentException
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.view.Home
import javafx.scene.layout.BorderPane
import tornadofx.*

class DeliveryEditor: Fragment("Modification de livraison"){

  val prevDelivery : Delivery by param()
  val parentView: Home by param()

  val startTime = textfield(prevDelivery.startTime.toString())
  val endTime = textfield(prevDelivery.endTime.toString())
  val hourDuration = textfield(prevDelivery.duration.hours.toString())
  val minDuration = textfield(prevDelivery.duration.minutes.toString())

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
        button("Valider") {
          action {
            parentView.controller.editDelivery(
                prevDelivery,
                Delivery(
                    prevDelivery.address,
                    startTime.text.toNullableInstant(),
                    endTime.text.toNullableInstant(),
                    Duration(
                        hourDuration.text.toInt(),
                        minDuration.text.toInt(),
                        0
                    )
                )
            )
            close()
          }
        }
      }
      right{
        button("Annuler") {
          action {
            close()
          }
        }
      }
    }
  }

  private fun String.toNullableInstant(): Instant? {
    if(this.isNullOrBlank()) return null
    if(this == "null") return null
    try {
      return this.toInstant()
    } catch (e: Exception) {
      throw InvalidArgumentException(arrayOf("Cannot parse $this to Instant. Please use the format HH:MM"))
    }
  }

}