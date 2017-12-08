package fr.insalyon.pld.agile.view.fragment

import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.util.txt.RoadSheetSerializer
import fr.insalyon.pld.agile.view.event.HighlightLocationEvent
import fr.insalyon.pld.agile.view.event.HighlightLocationInListEvent
import javafx.scene.control.Button
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import java.net.URI
import java.util.*

class RoundFragment : Fragment(), Observer {

  val controller: Controller by param()
  val parentView: BorderPane by param()
  val round = controller.round
  val roadSheetSerializer: RoadSheetSerializer = RoadSheetSerializer()

  val list = vbox {
    label("Livraisons") {
      paddingTop = 5.0
      prefHeight = 30.0
      paddingBottom = 15.0
      paddingLeft = 30.0
      style {
        fontWeight = FontWeight.BOLD
      }
    }
    round!!.deliveries().forEachIndexed { i, delivery ->
      hbox {
        paddingTop = 2
        label("${i + 2}.${if (i + 2 < 10) "  " else ""}") {
          paddingTop = 4
        }
        val addressId = delivery.address.id.toString()
        button(addressId) {
          id = addressId
          action {
            fire(HighlightLocationEvent(addressId, false))
          }
          style {
            baseColor = Color.WHITE
          }
        }
        label(deliveryToText(delivery)) {
          paddingTop = 4
        }
      }

    }
  }

  val container = vbox {
    vboxConstraints {
      paddingLeft = 30.0
    }
    label("Entrepôt") {
      paddingLeft = 30.0
      style {
        fontWeight = FontWeight.BOLD
      }
    }
    hbox {
      paddingTop = 10.0
      label("1.  ") {
        paddingTop = 4
      }
      val warehouseOfAddressId = round!!.warehouse.address.id.toString()
      button(warehouseOfAddressId) {
        id = warehouseOfAddressId
        action {
          fire(HighlightLocationEvent(warehouseOfAddressId, true))
        }
        style {
          baseColor = Color.WHITE
        }
      }
    }
    borderpane {
      paddingTop = 20.0
      minWidth = 350.0
      center {
        add(list)
      }
      right {
        vbox {
          hbox {
            spacing = 3.0
            paddingBottom = 10.0
            button {
              prefWidth = 30.0
              prefHeight = 30.0
              style {
                backgroundImage += URI.create("image/undo.png")
                backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
                backgroundPosition += BackgroundPosition.CENTER
              }
              action {
                controller.undo()
              }
            }
            button {
              prefWidth = 30.0
              prefHeight = 30.0
              style {
                backgroundImage += URI.create("image/redo.png")
                backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
                backgroundPosition += BackgroundPosition.CENTER
              }
              action {
                controller.redo()
              }
            }
          }
          round!!.deliveries().forEach { delivery ->
            hbox {
              spacing = 3.0
              paddingTop = 2
              button {
                prefWidth = 30.0
                prefHeight = 30.0
                style {
                  backgroundImage += URI.create("image/edit.png")
                  backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
                  backgroundPosition += BackgroundPosition.CENTER
                }
                action {
                  controller.editingOfDelivery(delivery)
                }
              }
              button {
                action { deleteDelivery(delivery) }
                prefWidth = 30.0
                prefHeight = 30.0
                style {
                  backgroundImage += URI.create("image/delete.png")
                  backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
                  backgroundPosition += BackgroundPosition.CENTER
                }
              }
            }
          }
        }
      }
    }
    label("Entrepôt") {
      paddingTop=20.0
      paddingLeft=30.0
      style{
        fontWeight = FontWeight.BOLD
      }
    }
    hbox {
      paddingTop = 10.0
      label("${round!!.deliveries().size + 2}.${if (round!!.deliveries().size + 2 < 10) "  " else ""}") {
        paddingTop = 4
      }
      val warehouseIdAddress = round!!.warehouse.address.id.toString()
      button(warehouseIdAddress) {
        id = warehouseIdAddress
        action {
          fire(HighlightLocationEvent(warehouseIdAddress, true))
        }
        style {
          baseColor = Color.WHITE
        }
      }
    }
    label("Feuille de route") {
      paddingLeft=30.0
      paddingTop=20.0
      style{
        fontWeight = FontWeight.BOLD
      }
    }
    hbox {
      paddingTop=10.0
      paddingLeft=30.0
      button("Génerer"){
        id=round!!.warehouse.address.id.toString()
        action{
          roadSheetSerializer.serializeHTML(round!!)
        }
        style {
          baseColor = Color.WHITE
        }
      }
    }
  }

  override val root = scrollpane {
    if (round != null) {
      add(container)
    }
  }

  private fun deliveryToText(d: Delivery): String {
    var res = ""
    if (d.startTime != null && d.endTime != null) {
      res += " : " + d.startTime.toFormattedString() + "-" + d.endTime.toFormattedString()
    }
    else if (d.startTime != null) {
      res += " A partir de : " + d.startTime.toFormattedString()
    }
    else if (d.endTime != null) {
      res += " Jusqu'à : " + d.endTime.toFormattedString()
    }
    return res
  }

  init {
    subscribe<HighlightLocationInListEvent> { event ->
      highlightLocation(event.id, event.color)
    }
    controller.round!!.addObserver(this)
  }

  private fun deleteDelivery(delivery: Delivery) {
    controller.deleteDelivery(delivery)
  }

  override fun update(o: Observable?, arg: Any?) {
    controller.round!!.deleteObserver(this)
    controller.window.refreshAll()
  }

  private fun highlightLocation(id: String, color: Color) {
    list.children
        .filter { it is HBox }
        .forEach {
          it.getChildList()!!
              .filter { it is Button && it.id == id }
              .forEach { button ->
                button.style {
                  baseColor = color
                }
              }
        }
  }
}