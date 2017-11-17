package fr.insalyon.pld.agile.controller

import com.sun.media.sound.InvalidFormatException
import fr.insalyon.pld.agile.controller.implementation.Controller
import fr.insalyon.pld.agile.controller.implementation.InitState
import fr.insalyon.pld.agile.getTestResourcePath
import org.junit.Test
import java.io.FileNotFoundException

class DefaulStateTest {

  val controller: Controller = Controller()


  /**
   * Try to load a plan from a non xml file
   */
  @Test(expected = InvalidFormatException::class)
  fun loadPlanInvalidFormatException() {
    val DEFAULT_STATE = InitState()
    DEFAULT_STATE.loadPlan(controller, getTestResourcePath("test.txt"))
  }

  /**
   * Try to load a plan from a non existing file
   */
  @Test(expected = FileNotFoundException::class)
  fun loadPlanNonExistingFile() {
    val DEFAULT_STATE = InitState()
    DEFAULT_STATE.loadPlan(controller, getTestResourcePath("fichiersXML/PlanXML/planLyonGran.xml"))
  }

  /**
   * Try to load an xml file which is not correctly formatted
   */
  @Test(expected = InvalidFormatException::class)
  fun loadPlanXMLInvalidFormat() {
    val DEFAULT_STATE = InitState()
    DEFAULT_STATE.loadPlan(controller, getTestResourcePath("fichiersXML/PlanXML/planLyonGrandSansNoeud.xml"))
  }


}