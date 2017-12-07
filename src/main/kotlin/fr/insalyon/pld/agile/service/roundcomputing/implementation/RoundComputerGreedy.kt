package fr.insalyon.pld.agile.service.roundcomputing.implementation

import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.roundmodifier.implementation.RoundModifierImp

class RoundComputerGreedy(
    plan: Plan,
    roundRequest: RoundRequest,
    speed: Speed
) : RoundComputerTemplate(plan, roundRequest, speed){

  companion object {
    val deliveryOrderer = getDeliveryComparator()
    private fun getDeliveryComparator(): Comparator<Delivery> = Comparator { deliveryA, deliveryB ->
      // A[null-null] ; B[null-null]
      if (deliveryA.hasNotStartTime() && deliveryA.hasEndTime() && deliveryB.hasNotStartTime() && deliveryB.hasNotEndTime()) return@Comparator 0
      // A[X,Y] ; B[null-null]
      if ((deliveryA.hasStartTime() || deliveryA.hasEndTime()) && (deliveryB.hasNotStartTime() && deliveryB.hasNotEndTime())) return@Comparator -1
      // A[null, null] ; B[X,Y]
      if ((deliveryA.hasNotStartTime() && deliveryA.hasNotEndTime()) && (deliveryB.hasStartTime() || deliveryB.hasEndTime())) return@Comparator 1
      // A[X,null] ; B[Y,null]
      if (deliveryA.hasStartTime() && deliveryB.hasStartTime() && deliveryA.hasNotEndTime() && deliveryB.hasNotEndTime()) {
        return@Comparator deliveryA.startTime!!.compareTo(deliveryB.startTime!!)
      }
      // A[null, X] ; B[null, X]
      if (deliveryA.hasNotStartTime() && deliveryB.hasNotStartTime() && deliveryA.hasEndTime() && deliveryB.hasEndTime()) {
        return@Comparator deliveryA.endTime!!.compareTo(deliveryB.endTime!!)
      }
      // A[X,Z] ; B[X, null]
      if (deliveryA.hasStartTime() && deliveryA.hasEndTime() && deliveryB.hasStartTime() && deliveryB.hasNotEndTime()) return@Comparator -1
      // A[X, null] ; B[Y, Z]
      if (deliveryA.hasStartTime() && deliveryA.hasNotEndTime() && deliveryB.hasStartTime() && deliveryB.hasEndTime()) return@Comparator 1
      // A[X,Y] ; B[S,T]
      if (deliveryA.hasStartTime() && deliveryA.hasEndTime() && deliveryB.hasStartTime() && deliveryB.hasEndTime()) {
        if (deliveryA.endTime!! < deliveryB.startTime!!) return@Comparator -1
        if (deliveryB.endTime!! < deliveryA.startTime!!) return@Comparator 1
        if (deliveryA.startTime!! < deliveryB.startTime!! && deliveryA.endTime!! < deliveryB.endTime!!) return@Comparator -1
        if (deliveryA.startTime!! < deliveryB.startTime!! && deliveryA.endTime!! > deliveryB.endTime!!) return@Comparator -1
        if (deliveryA.startTime!! > deliveryB.startTime!! && deliveryA.endTime!! < deliveryB.endTime!!) return@Comparator 1
        if (deliveryA.startTime!! > deliveryB.startTime!! && deliveryA.endTime!! > deliveryB.endTime!!) return@Comparator 1
      }
      0
    }
  }

  override fun compute(): Round {

    val subPlanInMeters = getSubPlan(plan, roundRequest)
    val subPlanInSeconds = subPlanInMeters.rescale(1.0 / speed.toMeterPerSeconds().value)

    val fixedDeliveries = getFixedDeliveries(roundRequest)
    val intersections = listOf(roundRequest.warehouse.address) + fixedDeliveries.map { it.address }
    val linkedSetOfDeliveries = fixedDeliveries.toLinkedHashSet()
    val linkedSetOfDurationPaths = buildDurationPath(intersections, subPlanInSeconds)
    val linkedSetOfDistancePaths = buildDistancePath(intersections, subPlanInMeters)

    var round = Round(roundRequest.warehouse, linkedSetOfDeliveries, linkedSetOfDurationPaths, linkedSetOfDistancePaths)

    val roundModifier = RoundModifierImp(plan)

    getUnfixedDeliveries(roundRequest).forEach {
      roundModifier.addDelivery(it, round)
    }

    return round

  }

  private fun getFixedDeliveries(roundRequest: RoundRequest): List<Delivery> {
    val fixedDeliveries = mutableListOf<Delivery>()
    for(i in roundRequest.deliveries.indices) {
      val delivery = roundRequest.deliveries.elementAt(i)
      if(delivery.hasStartTime() || delivery.hasEndTime()) {
        fixedDeliveries.add(delivery)
      }
    }
    return fixedDeliveries.sortedWith(deliveryOrderer)
  }

  private fun getUnfixedDeliveries(roundRequest: RoundRequest): List<Delivery> {
    return roundRequest.deliveries.filter { it.hasNotEndTime() && it.hasNotStartTime() }
  }

  private fun  buildIntersections(deliveries: List<Int>): List<Intersection> {
    val result = mutableListOf<Intersection>()
    deliveries.forEach { result += roundRequest.intersections[it] }
    return result
  }

}