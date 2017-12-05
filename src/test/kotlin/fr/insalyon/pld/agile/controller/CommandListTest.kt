package fr.insalyon.pld.agile.controller

import fr.insalyon.pld.agile.controller.api.Command
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import tornadofx.c

class CommandListTest {

  object X {
    var value: Int = 0
    fun reset() {
      value = 0
    }
  }

  object Cmd : Command {
    override fun doCommand() {
      X.value += 1
    }
    override fun undoCommand() {
      X.value -= 1
    }
  }

  object Cmd5 : Command {
    override fun doCommand() {
      X.value += 5
    }
    override fun undoCommand() {
      X.value -= 5
    }
  }

  @Before
  fun resetX() {
    X.reset()
  }

  @Test
  fun testAddCommand() {

    val commandList = CommandList()
    commandList.add(Cmd)
    commandList.add(Cmd)

    assertEquals(2, X.value)

  }

  @Test
  fun testUndoCommand() {
    val commandList = CommandList()
    commandList.add(Cmd)
    commandList.add(Cmd)
    commandList.add(Cmd)

    commandList.undo()

    assertEquals(2, X.value)

  }

  @Test
  fun testUndoEmptyCommandList() {
    val commandList = CommandList()
    commandList.undo()
    assertEquals(0, X.value)
  }

  @Test
  fun testRedoEmptyCommandList() {
    val commandList = CommandList()
    commandList.redo()
    assertEquals(0, X.value)
  }

  @Test
  fun testUndoAndRedo() {

    val commandList = CommandList()
    commandList.add(Cmd)
    assertEquals(1, X.value)

    commandList.undo()
    assertEquals(0, X.value)

    commandList.redo()
    assertEquals(1, X.value)

  }

  @Test
  fun testEraseCommandList() {

    val commandList = CommandList()
    commandList.add(Cmd)
    commandList.add(Cmd)
    commandList.add(Cmd)

    assertEquals(3, X.value)

    commandList.undo()
    commandList.undo()
    assertEquals(1, X.value)

    commandList.redo()
    assertEquals(2, X.value)

    commandList.add(Cmd5)
    assertEquals(7, X.value)
    commandList.redo()
    assertEquals(7, X.value)

  }

  @Test
  fun testErase() {

    val commandList = CommandList()
    commandList.add(Cmd)
    commandList.add(Cmd)
    commandList.add(Cmd)

    assertEquals(3, X.value)

    commandList.reset()

    commandList.undo()
    commandList.undo()
    assertEquals(3, X.value)

  }


}