package fr.insalyon.pld.agile.service.roundcomputing.api

import fr.insalyon.pld.agile.model.Round

/**
 * Compute the `Round` round
 */
interface RoundComputer {

  /**
   * Return a computed round
   */
  val round: Round

}