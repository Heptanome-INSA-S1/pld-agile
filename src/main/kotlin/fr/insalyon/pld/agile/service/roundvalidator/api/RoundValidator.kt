package fr.insalyon.pld.agile.service.roundvalidator.api

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round

interface RoundValidator {

    fun isValid(delivery:Delivery, round: Round) = getInvalidatedConstraints(delivery, round).isEmpty()
    fun getInvalidatedConstraints(delivery: Delivery, round: Round): Map<Delivery, String>

}