package fr.insalyon.pld.agile.controller

import com.sun.media.sound.InvalidFormatException
import fr.insalyon.pld.agile.controller.implementation.DefaultState
import org.junit.Test

class DefaulStateTest {

    /**
     * File without xml extension
     */
    @Test(expected = InvalidFormatException::class)
    fun loadPlanInvalidFormatException(){
        val DEFAULT_STATE = DefaultState()
        DEFAULT_STATE.loadPlan("view/Home.fxml")
    }
}