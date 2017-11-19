package fr.insalyon.pld.agile.view.fragments

import fr.insalyon.pld.agile.model.Plan
import javafx.scene.paint.Color
import tornadofx.*

class PlanFragment : Fragment(){
    val plan : Plan by param()

    var X_SIZE = 1000.0
    var Y_SIZE = 1000.0
    var SIZE = 1.0


    override val root = vbox {
        val stack = stackpane {
            scrollpane {
                println("Test")
                group {
                    plan.nodes.forEach{
                        val nodeX : Double = it.element.x/(plan.width*1.0) * X_SIZE
                        val nodeY : Double = it.element.y/(plan.height*1.0) * Y_SIZE
                        circle {
                            centerX = nodeX
                            centerY = nodeY
                            radius = SIZE
                            if (it.element.id == 1L)
                                fill=Color.RED
                        }
                        plan.outEdges[it.index].forEach{
                            line{
                                startX = nodeX
                                startY = nodeY
                                endX = it.to.element.x/(plan.width*1.0) * X_SIZE
                                endY = it.to.element.y/(plan.height*1.0) * Y_SIZE
                            }
                        }
                    }
                }
            }
        }
        hbox {
            button("Zoom +"){
                action {
                    X_SIZE += 100.0
                    Y_SIZE += 100.0
                    println("X : "+X_SIZE+" Y: "+Y_SIZE)



                }
            }
            button("Zoom -"){
                action{
                    X_SIZE -= 100.00
                    Y_SIZE -= 100.0
                    println("X : "+X_SIZE+" Y: "+Y_SIZE)
                }
            }
        }
        hbox {
            slider(0.0, 1000.0){

            }
        }
    }

}