package fr.insalyon.pld.agile.controller

import com.sun.media.sound.InvalidFormatException
import fr.insalyon.pld.agile.controller.implementation.DefaultState
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import java.nio.file.Paths

class DefaulStateTest {

    /**
     * Try to load a plan from a non xml file
     */
    @Test(expected = InvalidFormatException::class)
    fun loadPlanInvalidFormatException(){
        val DEFAULT_STATE = DefaultState()
        DEFAULT_STATE.loadPlan(System.getProperty("user.dir")+"\\src\\test\\resources\\test.txt")
    }

    /**
     * Try to load a plan from a non existing file
     */
    @Test(expected = FileNotFoundException::class)
    fun loadPlanNonExistingFile(){
        val DEFAULT_STATE = DefaultState()
        DEFAULT_STATE.loadPlan(System.getProperty("user.dir")+"\\src\\test\\resources\\fichiersXML\\PlanXML\\planLyonGran.xml")
    }

    /**
     * Try to load an xml file which is not correctly formatted
     */
    @Test(expected = InvalidFormatException::class)
    fun loadPlanXMLInvalidFormat(){
        val DEFAULT_STATE = DefaultState()
        DEFAULT_STATE.loadPlan(System.getProperty("user.dir")+"\\src\\test\\resources\\fichiersXML\\PlanXML\\planLyonGrandSansNoeud.xml")
    }



}