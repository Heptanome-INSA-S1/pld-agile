package fr.insalyon.pld.agile.service.roundmodifier.api

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.SubPath

interface RoundModifier {

    fun addDelivery(delivery: Delivery, round:Round)
    fun removeDelivery(delivery: Delivery, round:Round, i:Int)
    fun modifyDelivery(delivery: Delivery, round:Round, i:Int)

}