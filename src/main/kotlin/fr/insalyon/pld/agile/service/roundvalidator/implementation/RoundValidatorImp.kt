package fr.insalyon.pld.agile.service.roundvalidator.implementation

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.Warehouse
import fr.insalyon.pld.agile.service.roundvalidator.api.RoundValidator
import tornadofx.getProperty

/**
 * Implementation of the Round Validator Interface
 */
class RoundValidatorImp : RoundValidator {

  private var constraints: Map<Round, Map<Delivery, List<String>>> = mutableMapOf()

  private fun invalidConstraints(round: Round): Map<Delivery, List<String>> {
    val constraints = mutableMapOf<Delivery, List<String>>()
    var currentTime = round.warehouse.departureHour + round.durationPathInSeconds()[0]

    round.deliveries().forEachIndexed { i, delivery ->
      constraints[delivery] = mutableListOf()
      if (delivery.startTime != null && delivery.endTime != null && delivery.endTime - delivery.startTime < delivery.duration) {
        (constraints[delivery]!! as MutableList).add("Delivery: ${delivery.address.id}. Constraint duration is not valid")
      }

      if (delivery.startTime != null && delivery.startTime > currentTime) {
        currentTime = delivery.startTime
      }
      currentTime += delivery.duration

      if (delivery.endTime != null && delivery.endTime < currentTime) {
        (constraints[delivery]!! as MutableList).add("Delivery: ${delivery.address.id}. Constraint endtime is not respected")
      }

      currentTime += round.durationPathInSeconds()[i]

    }
    return constraints
  }


  override fun getInvalidatedConstraints(delivery: Delivery, round: Round): List<String> {
    return invalidConstraints(round)[delivery]!!
  }

  private fun <K, V> Map<K, V>.computeIfAbsent(key: K, func: () -> V): V? {
    if (this[key] == null) {
      this.plus(key to func())
    }
    return this[key]
  }

}