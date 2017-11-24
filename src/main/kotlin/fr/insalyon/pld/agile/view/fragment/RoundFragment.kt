package fr.insalyon.pld.agile.view.fragment

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.view.event.HighlightLocationEvent
import javafx.scene.layout.BorderPane
import javafx.scene.text.FontWeight
import tornadofx.*

class RoundFragment : View() {
    val parentView: BorderPane by param()
    val round: Round by param()

    override val root = vbox {
        vboxConstraints {
            paddingTop=60.0
            paddingLeft=30.0
            minWidth=250.0
        }
        label("Warehouse"){
            paddingLeft=30.0
            style{
                fontWeight = FontWeight.BOLD
            }
        }
        hbox {
            paddingTop=2
            label ("1. "){
                paddingTop=4
            }
            button(""+round.warehouse.address.id){
                action{
                    fire(HighlightLocationEvent(""+round.warehouse.address.id,true))
                }
            }
            label (" : "+ round.warehouse.departureHour.toFormattedString()){
                paddingTop=4
            }
        }
        label("")
        label("Deliveries") {
            paddingLeft=30.0
            style{
                fontWeight = FontWeight.BOLD
            }
        }
        for (i in round.deliveries().indices) {
            hbox {round.deliveries().elementAt(i).address.id
                paddingTop=2
                label (""+(i+2)+". "){
                    paddingTop=4
                }
                button(""+round.deliveries().elementAt(i).address.id){
                    action{
                        fire(HighlightLocationEvent(""+round.deliveries().elementAt(i).address.id,false))
                    }
                }
                label (deliveryToText(round.deliveries().elementAt(i))){
                    paddingTop=4
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
            paddingTop=2
            label (""+(round.deliveries().size+2)+". "){
                paddingTop=4
            }
            button(""+round.warehouse.address.id){
                action{
                    fire(HighlightLocationEvent(""+round.warehouse.address.id,true))
                }
            }
        }
    }
    init {

    }

    private fun deliveryToText(d: Delivery): String{
        var res = " ( "+d.duration+" )"
        if(d.startTime !=null && d.endTime !=null) {
            res += " : " + d.startTime.toFormattedString() + "-"+ d.endTime.toFormattedString()
        }
        return res
    }
}