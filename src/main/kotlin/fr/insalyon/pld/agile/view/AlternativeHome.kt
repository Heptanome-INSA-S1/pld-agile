package fr.insalyon.pld.agile.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import tornadofx.*

class AlternativeHome : View() {
    override val root = BorderPane()
    val counter = SimpleIntegerProperty()

    init {
        with(root) {
            style {
                padding = box(20.px)
            }

            center {
                vbox(10.0) {
                    alignment = Pos.CENTER

                    label() {
                        bind(counter)
                        style { fontSize = 25.px }
                    }

                    button("Click to increment").setOnAction {
                        increment()
                    }

                }
            }

        }
    }

    fun increment() {
        counter.value += 1
    }

}