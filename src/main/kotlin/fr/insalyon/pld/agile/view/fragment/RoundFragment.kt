package fr.insalyon.pld.agile.view.fragment

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.view.event.HighlightLocationEvent
import fr.insalyon.pld.agile.view.event.HighlightLocationInListEvent
import javafx.scene.control.Button
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import java.net.URI

class RoundFragment : Fragment() {
  val parentView: BorderPane by param()
  val round: Round? by param()

  val list = vbox {
    vboxConstraints {
      paddingLeft=30.0
    }
    label("Warehouse"){
      paddingLeft=30.0
      style{
        fontWeight = FontWeight.BOLD
      }
    }
    hbox {
      paddingTop=10.0
      label ("1.  "){
        paddingTop=4
      }
      button(""+round!!.warehouse.address.id){
        id=""+round!!.warehouse.address.id
        action{
          fire(HighlightLocationEvent(""+round!!.warehouse.address.id,true))
        }
        style{
          baseColor=Color.WHITE
        }
      }
      label (" : "+ round!!.warehouse.departureHour.toFormattedString()){
        paddingTop=4
      }
    }
    borderpane {
      paddingTop=20.0
      minWidth=350.0
      center{
        vbox{
          label("Deliveries") {
            paddingTop=5.0
            prefHeight=30.0
            paddingBottom=15.0
            paddingLeft=30.0
            style{
              fontWeight = FontWeight.BOLD
            }
          }
          for (i in round!!.deliveries().indices) {
            hbox {
              paddingTop=2
              label ("${i+2}.${if(i+2<10) "  " else ""}"){
                paddingTop=4
              }
              button(""+round!!.deliveries().elementAt(i).address.id){
                id = ""+round!!.deliveries().elementAt(i).address.id
                action{
                  fire(HighlightLocationEvent(""+round!!.deliveries().elementAt(i).address.id,false))
                }
                style{
                  baseColor=Color.WHITE
                }
              }
              label (deliveryToText(round!!.deliveries().elementAt(i))){
                paddingTop=4
              }
            }
          }
        }
      }
      right{
        vbox{
          hbox {
            spacing=3.0
            paddingBottom=10.0
            button{
              prefWidth=30.0
              prefHeight=30.0
              style{
                backgroundImage += URI.create( "image/add.png")
                backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT)
                backgroundPosition+= BackgroundPosition.CENTER
              }
            }
            button{
              prefWidth=30.0
              prefHeight=30.0
              style{
                backgroundImage += URI.create( "image/undo.png")
                backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT)
                backgroundPosition+= BackgroundPosition.CENTER
              }
            }
          }
          for (i in round!!.deliveries().indices) {
            hbox {
              spacing=3.0
              paddingTop=2
              button{
                prefWidth=30.0
                prefHeight=30.0
                style{
                  backgroundImage += URI.create( "image/edit.png")
                  backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT)
                  backgroundPosition+= BackgroundPosition.CENTER
                }
              }
              button{
                prefWidth=30.0
                prefHeight=30.0
                style{
                  backgroundImage += URI.create( "image/delete.png")
                  backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT)
                  backgroundPosition+= BackgroundPosition.CENTER
                }
              }
            }
          }
        }
      }
    }

    label("")
    label("Warehouse") {
      paddingLeft=30.0
      style{
        fontWeight = FontWeight.BOLD
      }
    }
    hbox {
      paddingTop=10.0
      label ("${round!!.deliveries().size+2}.${if(round!!.deliveries().size+2<10) "  " else ""}"){
        paddingTop=4
      }
      button(""+round!!.warehouse.address.id){
        id=""+round!!.warehouse.address.id
        action{
          fire(HighlightLocationEvent(""+round!!.warehouse.address.id,true))
        }
        style{
          baseColor=Color.WHITE
        }
      }
    }
  }

  override val root = scrollpane {
    add(list)
  }

  private fun deliveryToText(d: Delivery): String{
    var res = " ( "+d.duration+" )"
    if(d.startTime !=null && d.endTime !=null) {
      res += " : " + d.startTime.toFormattedString() + "-"+ d.endTime.toFormattedString()
    }
    return res
  }

  init {
    subscribe<HighlightLocationInListEvent> {
      event -> highlightLocation(event.id,event.color)
    }
  }

  private fun highlightLocation(id:String,color:Color){
    list.children
            .filter { it is HBox }
            .forEach {
              it.getChildList()!!
                      .filter { it is Button && it.id == id}
                      .forEach{ button ->
                        button.style {
                          baseColor = color
                        }
                      }
            }
  }

}