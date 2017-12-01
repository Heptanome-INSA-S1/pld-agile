package fr.insalyon.pld.agile.service.roundvalidator.implementation

import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.model.Round
import fr.insalyon.pld.agile.model.Warehouse
import fr.insalyon.pld.agile.service.roundvalidator.api.RoundValidator
import tornadofx.getProperty

class RoundValidatorImp (
    val round: Round
) : RoundValidator
{
    override fun getInvalidatedConstraints(delivery: Delivery,round: Round): Map<Delivery, String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}