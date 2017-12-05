package fr.insalyon.pld.agile.service.roundcomputing.implementation

import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.implementation.TSP1
import fr.insalyon.pld.agile.service.algorithm.implementation.TSP1WithTimeSlot
import fr.insalyon.pld.agile.service.roundcomputing.api.RoundComputer
import org.junit.Assert.assertEquals
import org.junit.Test

class RoundComputerImplTest {

  val source = Intersection(2, 0, 0)

  val node1 = Intersection(1, 0, 0)
  val node3 = Intersection(3, 0, 0)
  val node4 = Intersection(4, 0, 0)
  val node5 = Intersection(5, 0, 0)
  val node6 = Intersection(6, 0, 0)

  val plan by lazy {

    val roadOfLength1 = Junction(1000, "")
    val roadOfLength2 = Junction(2000, "")
    val roadOfLength4 = Junction(4000, "")

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

  val roundRequest = RoundRequest(
      Warehouse(address = source, departureHour = 8 h 0 m 0),
      setOf(
          Delivery(node4, duration = 200.seconds),
          Delivery(node5, duration = 400.seconds)
      )
  )

  @Test
  fun compute() {

    val roundComputer: RoundComputer = RoundComputerImpl(plan = plan,roundRequest =  roundRequest, tsp = TSP1WithTimeSlot(roundRequest), speed = 15.km_h)
    val round = roundComputer.round

    assertEquals(source, round.warehouse.address)
    assertEquals(node4, round.deliveries().first().address)
    assertEquals(node5, round.deliveries().last().address)

  }

}