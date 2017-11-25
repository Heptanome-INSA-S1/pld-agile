package fr.insalyon.pld.agile.service.roundmodifier.implementation

import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.implementation.DijsktraImpl
import fr.insalyon.pld.agile.service.roundmodifier.api.RoundModifier

class RoundModifierImp(
    val delivery: Delivery,
    var round: Round,
    private val plan:Plan
) : RoundModifier{

    override fun addDelivery(delivery: Delivery, round: Round) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeDelivery(delivery: Delivery, round: Round) {
        round.deliveries().forEachIndexed{ i,e ->
            if(e.equals(delivery)){
                var dijsktra: DijsktraImpl<Intersection, Junction>
                if(i !=0)
                    dijsktra = DijsktraImpl<Intersection, Junction>(plan, round.deliveries().elementAt(i - 1).address)
                else
                    dijsktra = DijsktraImpl<Intersection, Junction>(plan, round.warehouse.address)

                if(i!= round.deliveries().size-1)
                    round.removeDelivery(delivery,dijsktra.getShortestPath(round.deliveries().elementAt(i+1).address))
                else
                    round.removeDelivery(delivery,dijsktra.getShortestPath(round.warehouse.address))
            }
        }

    }

    override fun modifyDelivery(delivery: Delivery, round: Round) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}