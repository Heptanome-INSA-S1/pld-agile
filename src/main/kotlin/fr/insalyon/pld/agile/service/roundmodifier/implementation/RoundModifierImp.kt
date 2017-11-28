package fr.insalyon.pld.agile.service.roundmodifier.implementation

import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.algorithm.implementation.DijsktraImpl
import fr.insalyon.pld.agile.service.roundmodifier.api.RoundModifier

class RoundModifierImp(
    private val delivery: Delivery,
    private var round: Round,
    private val plan:Plan
) : RoundModifier{

    override fun addDelivery(delivery: Delivery, round: Round) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeDelivery(delivery: Delivery, round: Round, i:Int) {

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

    override fun modifyDelivery(delivery: Delivery, round: Round, i: Int) {
        //TODO("not implemented yet")
            var previousStartTime : Instant?
            var previousEndTime : Instant?
            var nextStartTime : Instant?
            var previousDuration : Long?

            var previousTravellingTime : Long
            var nextTravellingTime : Long

            // set specific values if the previous or the next element is the warehouse
            if(i==0){
                previousStartTime = round.warehouse.departureHour
                previousEndTime = null
                previousDuration = 0L
                previousTravellingTime = computeTravellingTime(round.warehouse,delivery,round)
                nextTravellingTime = computeTravellingTime(delivery, round.deliveries().elementAt(i+1), round)
                nextStartTime = round.deliveries().elementAt(i+1).startTime
            } else if (i == round.deliveries().size-1) {
                nextStartTime = null
                nextTravellingTime = computeTravellingTime(delivery, round.warehouse, round)
                previousStartTime = round.deliveries().elementAt(i-1).startTime
                previousEndTime = round.deliveries().elementAt(i-1).endTime
                previousDuration = round.deliveries().elementAt(i-1).duration.length
                previousTravellingTime = computeTravellingTime(round.deliveries().elementAt(i-1),delivery, round)
                nextTravellingTime = computeTravellingTime(delivery, round.warehouse, round)
                nextStartTime = null
            } else {
                previousStartTime = round.deliveries().elementAt(i-1).startTime
                previousEndTime = round.deliveries().elementAt(i-1).endTime
                previousStartTime = round.deliveries().elementAt(i-1).startTime
                nextStartTime = round.deliveries().elementAt(i+1).startTime
                previousDuration = round.deliveries().elementAt(i-1).duration.length
                previousTravellingTime = computeTravellingTime(round.deliveries().elementAt(i-1),delivery, round)
                nextTravellingTime = computeTravellingTime(delivery, round.deliveries().elementAt(i+1), round)
                nextStartTime = round.deliveries().elementAt(i+1).startTime
            }

            var isFirstPartValid =true
            var isSecondPartValid = true

            if(isDeliveryValid(delivery)){
                // check previous contraints
                if(delivery.startTime != null && previousEndTime != null && delivery.startTime < previousEndTime + previousTravellingTime.seconds ){
                    isFirstPartValid = false
                }
                if(delivery.startTime!= null && previousStartTime != null && isFirstPartValid &&  delivery.startTime!! < previousStartTime + previousDuration.seconds + previousTravellingTime.seconds){
                    isFirstPartValid = false
                }
                println("first part : " +  isFirstPartValid)

                //Check next constraints
                if(nextStartTime!= null && delivery.startTime!= null && nextStartTime < delivery.startTime + previousDuration.seconds + nextTravellingTime.seconds){
                    isSecondPartValid = false
                }
                if(isSecondPartValid && delivery.endTime != null && nextStartTime != null && nextStartTime < delivery.endTime + nextTravellingTime.seconds){
                    isSecondPartValid = false
                }
                println("second part : " + isSecondPartValid)

                if(isFirstPartValid && isSecondPartValid) {
                    round.modify(i,delivery.startTime, delivery.endTime, delivery.duration)
                }
            }
    }

    /**
     * @param from : the delivery from the one you want to compute the time
     * @param to : the next delivery after from
     * @param round : the round you have to consider
     *
     * @return  the travelling time between the two given deliveries
     */
    private fun computeTravellingTime(from:Delivery, to:Delivery, round: Round): Long {
        var res = 0L
        round.path().forEach { p->
            if(p.nodes.first() == from.address && p.nodes.last() == to.address){
                p.edges.forEach { e-> res+=e.length }
            }
        }
        return res
    }

    /**
     * @param from : the warehouse from which you want to compute the time
     * @param to : the next delivery after from
     * @param round : the round you have to consider
     *
     * @return  the travelling time between the two given deliveries
     */
    private fun computeTravellingTime(from:Warehouse, to:Delivery, round: Round): Long {
        var res = 0L
        round.path().forEach { p->
            if(p.nodes.first() == from.address && p.nodes.last() == to.address){
                p.edges.forEach { e-> res+=e.length }
            }
        }
        return res
    }

    /**
     * @param from : the delivery from the one you want to compute the time
     * @param to : the warehouse you have to reach after from
     * @param round : the round you have to consider
     *
     * @return  the travelling time between the two given deliveries
     */
    private fun computeTravellingTime(from:Delivery, to:Warehouse, round: Round): Long {
        var res = 0L
        round.path().forEach { p->
            if(p.nodes.first() == from.address && p.nodes.last() == to.address){
                p.edges.forEach { e-> res+=e.length }
            }
        }
        return res
    }

    private fun isDeliveryValid(delivery: Delivery) : Boolean {
        if(delivery.startTime != null && delivery.endTime !=null){
            if(delivery.startTime.plus(Duration(0,0,delivery.duration.toSeconds().toInt())).compareTo(delivery.endTime) <0){
                return true
            }
            return false
        }
        return true
    }

}

