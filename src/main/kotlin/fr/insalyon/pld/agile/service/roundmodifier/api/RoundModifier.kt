package fr.insalyon.pld.agile.service.roundmodifier.api

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*

interface RoundModifier {

    fun addDelivery(delivery: Delivery, round:Round)
    fun removeDelivery(i:Int, round:Round, speed: Speed = Config.defaultSpeed)
    fun modifyDelivery(delivery: Delivery, round:Round, i:Int)

}