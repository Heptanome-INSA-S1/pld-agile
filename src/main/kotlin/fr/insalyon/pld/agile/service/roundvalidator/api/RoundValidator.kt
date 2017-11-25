package fr.insalyon.pld.agile.service.roundvalidator.api

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round

interface RoundValidator {

    fun isValid(round: Round) = getInvalidatedConstraints(round).isEmpty()
    fun getInvalidatedConstraints(round: Round): Map<Delivery, String>

}