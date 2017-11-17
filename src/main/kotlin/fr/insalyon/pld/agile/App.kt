package fr.insalyon.pld.agile

import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.view.Home
import javafx.application.Application

// Create the default start application
class App : tornadofx.App(Home::class)

fun main(args: Array<String>) {

  val controller = Controller()
  controller.loadPlan(getResourcePath("xml/planLyonPetit.xml"))

  Application.launch(App::class.java, *args)
}