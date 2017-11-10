package fr.insalyon.pld.agile

import fr.insalyon.pld.agile.view.Home
import javafx.application.Application

// Create the default start application
class App: tornadofx.App(Home::class)

fun main(args: Array<String>) {
    // Start the application
    Application.launch(App::class.java, *args)
}