package fr.insalyon.pld.agile.controller.commands

import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.service.roundmodifier.implementation.RoundModifierImp
import fr.insalyon.pld.agile.util.Logger

class EditDelivery(
    private val roundModifier: RoundModifierImp,
    private val controller: Controller,
    private val prevDelivery: Delivery,
    private val newDelivery: Delivery
) : Command{
  private val round = controller.round!!
  private val index = round.deliveries().indexOf(prevDelivery)

  override fun doCommand() {
    Logger.debug(index)
    roundModifier.modifyDelivery(newDelivery, round, index)
  }

  override fun undoCommand() {
    round.modify(index, prevDelivery.startTime, prevDelivery.endTime, prevDelivery.duration)
  }
}