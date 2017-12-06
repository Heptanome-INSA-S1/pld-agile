package fr.insalyon.pld.agile.service.roundmodifier.api

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.model.*

/**
 * Modifier of round
 */
interface RoundModifier {

  fun addDelivery(delivery: Delivery, round: Round)
  fun removeDelivery(i: Int, round: Round, speed: Speed = Config.DEFAULT_SPEED)
  fun modifyDelivery(delivery: Delivery, round: Round, i: Int)

}