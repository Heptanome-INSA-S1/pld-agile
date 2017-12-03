package fr.insalyon.pld.agile.view.fragment


import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Instant
import fr.insalyon.pld.agile.util.Logger
import fr.insalyon.pld.agile.view.model.DeliveryModel
import javafx.beans.property.Property
import tornadofx.*

class DeliveryEditor: Fragment("Modification de livraison"){

  //val model : DeliveryModel by inject()
  val delivery : Delivery? by param()

  //val model : DeliveryModel = DeliveryModel
  override val root: Form = form {
    //Logger.debug(delivery!!.startTime.toString())
    fieldset("Créneau de livraison :") {
      field("Debut :") {
        //textfield(if(model.startTime.value != null) model.startTime.value.toString() else "")
        textfield(delivery!!.startTime.toString())
      }
      field("Fin :") {
        //textfield(if(model.endTime.value != null) model.endTime.value.toString() else "")
        textfield(delivery!!.endTime.toString())
      }


      button("Enregistrer") {
        //enableWhen(model.dirty)
        action {
        }
      }

      button("Reinitialiser") {
        /*enableWhen(model.dirty)
        action {
          model.rollback()
        }*/
      }
    }
  }

}