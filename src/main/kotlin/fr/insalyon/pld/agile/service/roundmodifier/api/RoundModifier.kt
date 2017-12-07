package fr.insalyon.pld.agile.service.roundmodifier.api

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.Speed

/**
 * Modifier of round
 */
interface RoundModifier {

  fun addDelivery(delivery: Delivery, round: Round)
  fun removeDelivery(i: Int, round: Round, speed: Speed = Config.Business.DEFAULT_SPEED)
  fun modifyDelivery(delivery: Delivery, round: Round, i: Int)

}