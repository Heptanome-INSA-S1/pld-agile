package fr.insalyon.pld.agile.controller.commands

import fr.insalyon.pld.agile.controller.api.Command
import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.lib.graph.model.Path
import fr.insalyon.pld.agile.model.*
import fr.insalyon.pld.agile.service.roundmodifier.implementation.RoundModifierImp
import fr.insalyon.pld.agile.util.Logger
import sun.rmi.runtime.Log

class RemoveDelivery(
    private val roundModifier: RoundModifierImp,
    private val controller: Controller,
    delivery: Delivery
) : Command {

  private val oldPath: SubPath
  private val index = controller.round!!.deliveries().indexOf(delivery)
  private val round = controller.round!!
  private var replacementPath: Path<Intersection, Junction>? = null
  private var replacementDuration: Duration? = null

  init {
    val index = round.deliveries().indexOf(delivery)
    val pathBefore = round.distancePathInMeters().elementAt(index)
    val pathAfter = round.distancePathInMeters().elementAt(index+1)
    val durationPathBefore = round.durationPathInSeconds()[index]
    val durationPathAfter = round.durationPathInSeconds()[index+1]

    oldPath = SubPath(
        pathBefore,
        durationPathBefore,
        delivery,
        pathAfter,
        durationPathAfter
    )
  }


  override fun doCommand() {
    Logger.debug(round.toTrace())
    roundModifier.removeDelivery(index, round)
    Logger.debug(round.toTrace())
  }

  override fun undoCommand() {
    Logger.debug(round.toTrace())
    round.addDelivery(oldPath)
    Logger.debug(round.toTrace())
  }
}