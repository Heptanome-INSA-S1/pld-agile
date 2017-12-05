package fr.insalyon.pld.agile.view.model

import fr.insalyon.pld.agile.model.Delivery
import tornadofx.*

class DeliveryModel : ItemViewModel<Delivery>() {

  val startTime = bind(Delivery::startTime)
  val endTime =  bind(Delivery::endTime)
  val duration =  bind(Delivery::duration)
  val address =  bind(Delivery::address)
  /*val endTime = bind { item?.endTime?.toProperty() }
  val duration = bind { item?.duration?.toProperty() }
  val address = bind { item?.address?.toProperty()}*/
}