package fr.insalyon.pld.agile.service.roundvalidator.api

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round

/**
 * Round validator interface
 */
interface RoundValidator {

  /**
   * Check if the constraints of the delivery `delivery` are checked in the round `round`
   */
  fun isValid(delivery: Delivery, round: Round) = getInvalidatedConstraints(delivery, round).isEmpty()

  /**
   * Check if a round is valid
   */
  fun isValid(round: Round) = round.deliveries().all { isValid(it, round) }

  /**
   * Return the constraint error messages of the delivery `delivery` for the round `round`. If all the constraints are
   * checked, it will return an empty list.
   */
  fun getInvalidatedConstraints(delivery: Delivery, round: Round): List<String>

}