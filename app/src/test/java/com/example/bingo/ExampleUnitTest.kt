package com.example.bingo

import com.example.bingo.domain.*
import org.junit.Assert.*
import org.junit.Test

class TaskUnitTest {

    @Test
    fun simpleTask_completeAndReset() {
        val task = Task(1, "Test Task")
        val simpleTask = SimpleTask(task)

        assertFalse(task.isCompleted)

        simpleTask.complete()
        assertTrue(task.isCompleted)

        simpleTask.reset()
        assertFalse(task.isCompleted)
    }

    @Test
    fun advancedTask_addRemoveCompleteReset() {
        val task1 = Task(1, "Task 1")
        val task2 = Task(2, "Task 2")
        val advancedTask = AdvancedTask(mutableListOf(task1, task2))

        advancedTask.complete()
        assertTrue(task1.isCompleted)
        assertTrue(task2.isCompleted)

        advancedTask.reset()
        assertFalse(task1.isCompleted)
        assertFalse(task2.isCompleted)

        advancedTask.removeTask(1)
        assertEquals(1, advancedTask.getAllTasks().size)
        assertEquals(task2, advancedTask.getAllTasks()[0])
    }

    @Test
    fun bingoTask_completeAndResetGrid() {
        val t1 = Task(1, "A")
        val t2 = Task(2, "B")
        val t3 = Task(3, "C")
        val t4 = Task(4, "D")
        val t5 = Task(5, "E")
        val t6 = Task(6, "F")
        val t7 = Task(7, "G")
        val t8 = Task(8, "H")
        val t9 = Task(9, "I")

        val grid = arrayOf(
            arrayOf(t1, t2, t3),
            arrayOf(t4, t5, t6),
            arrayOf(t7, t8, t9)
        )

        val bingoTask = BingoTask(grid)

        bingoTask.completeTaskAt(0, 0)
        assertTrue(t1.isCompleted)
        assertFalse(t2.isCompleted)

        bingoTask.resetGrid()
        grid.flatten().forEach { assertFalse(it.isCompleted) }
    }
}

class BingoManagerUnitTest {

    @Test
    fun addRemoveCompleteResetTasks() {
        val manager = BingoManager()

        manager.addTask("Task 1")
        manager.addTask("Task 2")

        val all = manager.getAllTasks()
        assertEquals(2, all.size)

        val id1 = all[0].id
        manager.completeTask(id1)
        assertTrue(manager.getAllTasks()[0].isCompleted)
        assertFalse(manager.getAllTasks()[1].isCompleted)

        manager.resetCompletion()
        manager.getAllTasks().forEach { assertFalse(it.isCompleted) }

        manager.removeTask(id1)
        assertEquals(1, manager.getAllTasks().size)
    }

    @Test
    fun generateBingoGrid_correctSizeAndTasks() {
        val manager = BingoManager()
        repeat(9) { i -> manager.addTask("Task ${i+1}") }

        val grid = manager.generateBingoGrid()
        assertEquals(3, grid.size)
        grid.forEach { row -> assertEquals(3, row.size) }

        val flattened = grid.flatten().toSet()
        assertEquals(9, flattened.size)
    }

    @Test(expected = IllegalStateException::class)
    fun generateBingoGrid_notEnoughTasks() {
        val manager = BingoManager()
        repeat(5) { i -> manager.addTask("Task ${i+1}") }
        manager.generateBingoGrid()
    }

    @Test
    fun getTasksDueSoon_filtersCorrectly() {
        val now = System.currentTimeMillis()
        val manager = BingoManager()
        manager.addTask("Task 1", dueDate = now + 1000)
        manager.addTask("Task 2", dueDate = now + 10_000_000)
        manager.addTask("Task 3")

        val dueSoon = manager.getTasksDueSoon(now, thresholdMillis = 5000)
        assertEquals(1, dueSoon.size)
        assertEquals("Task 1", dueSoon[0].text)
    }
}
