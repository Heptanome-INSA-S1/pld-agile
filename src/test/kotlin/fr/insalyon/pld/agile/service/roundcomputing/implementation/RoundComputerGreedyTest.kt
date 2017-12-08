package fr.insalyon.pld.agile.service.roundcomputing.implementation

import fr.insalyon.pld.agile.Config
import fr.insalyon.pld.agile.getTestResource
import fr.insalyon.pld.agile.util.toPlan
import fr.insalyon.pld.agile.util.toRoundRequest
import org.junit.Test

class RoundComputerGreedyTest {

  @Test
  fun testCalculateRound() {

    val plan = getTestResource("fichiersXML/planLyonMoyen.xml").toPlan()
    val roundRequest = getTestResource("fichiersXML/DLmoyen5TW4.xml").toRoundRequest(plan)
    val roundComputer = RoundComputerGreedy(plan, roundRequest, Config.Business.DEFAULT_SPEED)
    val round = roundComputer.round


  }

}