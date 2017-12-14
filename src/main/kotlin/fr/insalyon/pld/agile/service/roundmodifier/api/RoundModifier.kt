package fr.insalyon.pld.agile.service.roundmodifier.api

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.Speed

/**
 * Modifier of round
 */
interface RoundModifier {

  /**
   * Add a delivery to a round
   */
  fun addDelivery(delivery: Delivery, round: Round)

  /**
   * Remove a delivery to a round. Speed is used to compute the shortest alternative path
   */
  fun removeDelivery(i: Int, round: Round, speed: Speed = Config.Business.DEFAULT_SPEED)

  /**
   * Modify the delivery at position i in the round with de `delivery` attributes
   */
  fun modifyDelivery(delivery: Delivery, round: Round, i: Int)

}