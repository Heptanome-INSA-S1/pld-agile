package fr.insalyon.pld.agile.controller.commands

import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.model.Delivery
import fr.insalyon.pld.agile.service.roundmodifier.implementation.RoundModifierImp

class SaveDelivery(
    private val roundModifier: RoundModifierImp,
    private val controller: Controller,
    private val delivery: Delivery
) : Command {

  private val round = controller.round!!

  override fun doCommand() {
    roundModifier.addDelivery(delivery, round)
  }

  override fun undoCommand() {
    roundModifier.removeDelivery(controller.round!!.deliveries().indexOf(delivery), controller.round!!)
  }
}