package fr.insalyon.pld.agile.view.fragment

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round
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
        label(""+round.warehouse.address.id+" : "+ round.warehouse.departureHour.toBeautifulString())
        label("")
        label("Deliveries") {
            paddingLeft=30.0
            style{
                fontWeight = FontWeight.BOLD
            }
        }
        for (i in round.deliveries().indices) {
            label(""+(i+1)+" "+deliveryToText(round.deliveries().elementAt(i)))
        }
        label("")
        label("Warehouse") {
            paddingLeft=30.0
            style{
                fontWeight = FontWeight.BOLD
            }
        }
        label(""+round.warehouse.address.id)
    }
    init {

    }

    private fun deliveryToText(d: Delivery): String{
        var res = ""+d.address.id+" ( "+d.duration+" )"
        if(d.startTime !=null && d.endTime !=null) {
            res += " : " + d.startTime.toBeautifulString() + "-"+ d.endTime.toBeautifulString()
        }
        return res
    }
}