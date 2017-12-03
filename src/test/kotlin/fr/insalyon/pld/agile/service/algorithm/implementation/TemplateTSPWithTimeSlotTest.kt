package fr.insalyon.pld.agile.service.algorithm.implementation

import fr.insalyon.pld.agile.lib.graph.model.Graph
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.roundcomputing.api.RoundComputer
import fr.insalyon.pld.agile.service.roundcomputing.implementation.RoundComputerImpl
import org.junit.Assert.assertEquals
import org.junit.Test

class TemplateTSPWithTimeSlotTest {

  val source = Intersection(2,0,0)
  val node1 = Intersection(1,0,0)
  val node3 = Intersection(3,0,0)
  val node4 = Intersection(4,0,0)
  val node5 = Intersection(5,0,0)
  val node6 = Intersection(6,0,0)

  val plan by lazy {

    val roadOfLength1 = Junction(10.minutes.toSeconds(), "")
    val roadOfLength2 = Junction(20.minutes.toSeconds(), "")
    val roadOfLength4 = Junction(40.minutes.toSeconds(), "")

    Plan(
        setOf<Intersection>(node1, source, node3, node4, node5, node6),
        setOf(
            Triple(node1, roadOfLength1, source),
            Triple(node1, roadOfLength4, node3),
            Triple(source, roadOfLength1, node4),
            Triple(source, roadOfLength2, node5),
            Triple(node4, roadOfLength1, node3),
            Triple(node4, roadOfLength2, node5),
            Triple(node5, roadOfLength1, node6),
            Triple(node6, roadOfLength1, source)
        )
    )
  }

  fun getSubPlan(plan: Plan, roundRequest: RoundRequest): Graph<Intersection, Path<Intersection, Junction>> {
    val nodes = mutableSetOf<Intersection>()
    val roads = mutableSetOf<Triple<Intersection, Path<Intersection, Junction>, Intersection>>()

    for(source: Intersection in roundRequest.intersections) {
      val dijsktra = Dijkstra<Intersection, Junction>(plan, source)
      val destinations = roundRequest.intersections.filter { it != source }
      for(destination: Intersection in destinations) {
        nodes.add(source)
        nodes.add(destination)
        roads.add(Triple(source, dijsktra.getShortestPath(destination), destination))
      }
    }
    return Graph(nodes, roads)
  }

  fun computeTour(plan: Plan, roundRequest: RoundRequest): Round {
    val roundComputer: RoundComputer = RoundComputerImpl(plan, roundRequest, TSP1WithTimeSlot(roundRequest), 1.m_s)
    return roundComputer.round
  }


  @Test
  fun testCompute() {

    val warehouse = Warehouse(address = source, departureHour = 8 h 0)
    val deliveryA = Delivery(address = node4, duration = 15.minutes)
    val deliveryB = Delivery(address = node5, duration = 20.minutes)
    val roundRequest = RoundRequest(warehouse, linkedSetOf(deliveryA, deliveryB))
    assertEquals(85.minutes.toSeconds(), computeTour(plan, roundRequest).length)

  }

  @Test
  fun testCompute2() {

    val warehouse = Warehouse(address = source, departureHour = 8 h 0)
    val deliveryA = Delivery(address = node4, startTime = 8 h 15, duration = 15.minutes)
    val deliveryB = Delivery(address = node5, duration = 20.minutes)
    val roundRequest = RoundRequest(warehouse, linkedSetOf(deliveryA, deliveryB))
    assertEquals(90.minutes.toSeconds(), computeTour(plan, roundRequest).length)

  }

  @Test
  fun testCompute3() {

    val warehouse = Warehouse(address = source, departureHour = 8 h 0)
    val deliveryA = Delivery(address = node4, startTime = 10 h 0, duration = 15.minutes)
    val deliveryB = Delivery(address = node5, duration = 20.minutes)
    val roundRequest = RoundRequest(warehouse, linkedSetOf(deliveryA, deliveryB))
    assertEquals(2.hours + 55.minutes, computeTour(plan, roundRequest).length.seconds)

  }

  @Test
  fun testCompute4() {

    val warehouse = Warehouse(address = source, departureHour = 8 h 0)
    val deliveryA = Delivery(address = node4, startTime = 10 h 0, duration = 15.minutes)
    val deliveryB = Delivery(address = node5, startTime = 9 h 30, endTime = 10 h 0, duration = 20.minutes)
    val roundRequest = RoundRequest(warehouse, linkedSetOf(deliveryA, deliveryB))
    assertEquals(3.hours + 15.minutes, computeTour(plan, roundRequest).length.seconds)

  }

  @Test
  fun testCompute6() {

    val warehouse = Warehouse(address = source, departureHour = 8 h 0)
    val deliveryA = Delivery(address = node4, duration = 15.minutes)
    val deliveryB = Delivery(address = node5, endTime = 9 h 0, duration = 20.minutes)
    val roundRequest = RoundRequest(warehouse, linkedSetOf(deliveryA, deliveryB))
    assertEquals(2.hours + 5.minutes, computeTour(plan, roundRequest).length.seconds)

  }

  @Test(expected = RuntimeException::class)
  fun testCompute5() {
    val warehouse = Warehouse(address = source, departureHour = 8 h 0)
    val deliveryA = Delivery(address = node4, startTime = 10 h 15, endTime = 10 h 45, duration = 15.minutes)
    val deliveryB = Delivery(address = node5, startTime = 10 h 0, endTime = 11 h 0, duration = 20.minutes)
    val roundRequest = RoundRequest(warehouse, linkedSetOf(deliveryA, deliveryB))
    computeTour(plan, roundRequest)
  }

}