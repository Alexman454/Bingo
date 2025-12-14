package com.example.bingo.domain

import androidx.compose.runtime.mutableStateListOf
sealed class DisplayableTask {
    data class Simple(val simpleTask: SimpleTask) : DisplayableTask()
    data class Advanced(val advancedTask: AdvancedTask) : DisplayableTask()
    data class Bingo(val bingoTask: BingoTask) : DisplayableTask()
}
data class Task(
    val id: Int,
    val text: String,
    var isCompleted: Boolean = false,
    var dueDate: Long? = null
)

/**
 * Простая задача
 */
data class SimpleTask(val task: Task) {
    fun complete() {
        task.isCompleted = true
    }

    fun reset() {
        task.isCompleted = false
    }

    fun info(): String = "${task.id}: ${task.text} [${if (task.isCompleted) "✓" else " "}]"
}

/**
 * Продвинутая задача с подзадачами
 */
class AdvancedTask(
    val tasks: MutableList<Task> = mutableListOf()
) {
    // Для Compose UI
    var stateTasks = mutableStateListOf<Task>().apply { addAll(tasks) }

    fun addTask(task: Task) {
        tasks.add(task)
        stateTasks.add(task)
    }

    fun removeTask(id: Int) {
        tasks.removeAll { it.id == id }
        stateTasks.removeAll { it.id == id }
    }

    fun complete() {
        tasks.forEach { it.isCompleted = true }
        stateTasks.forEach { it.isCompleted = true }
    }

    fun reset() {
        tasks.forEach { it.isCompleted = false }
        stateTasks.forEach { it.isCompleted = false }
    }

    fun getAllTasks(): List<Task> = tasks
}

/**
 * Бинго-сетка
 */
class BingoTask(
    val bingoGrid: Array<Array<Task>>
) {
    var stateGrid: Array<Array<Task>> = Array(3) { row ->
        Array(3) { col -> bingoGrid[row][col] }
    }

    fun completeTaskAt(row: Int, col: Int) {
        stateGrid[row][col].isCompleted = true
    }

    fun resetGrid() {
        for (row in stateGrid) {
            for (task in row) {
                task.isCompleted = false
            }
        }
    }

    fun printGrid() {
        for (row in stateGrid) {
            println(row.joinToString(" | ") { if (it.isCompleted) "✓ ${it.text}" else it.text })
        }
    }
}

/**
 * Класс, управляющий списком задач и формированием бинго-сетки
 */
class BingoManager {
    private val tasks = mutableListOf<Task>()
    private var idCounter = 1

    fun addTask(text: String, dueDate: Long? = null) {
        if (text.isBlank()) return
        tasks.add(Task(id = idCounter++, text = text.trim(), dueDate = dueDate))
    }

    fun removeTask(id: Int) {
        tasks.removeAll { it.id == id }
    }

    fun getAllTasks(): List<Task> = tasks.toList()

    fun completeTask(id: Int) {
        tasks.find { it.id == id }?.isCompleted = true
    }

    fun resetCompletion() {
        tasks.forEach { it.isCompleted = false }
    }

    fun generateBingoGrid(): Array<Array<Task>> {
        if (tasks.size < 9) throw IllegalStateException("Недостаточно задач: нужно минимум 9")
        val selected = tasks.shuffled().take(9)
        return Array(3) { row ->
            Array(3) { col ->
                selected[row * 3 + col]
            }
        }
    }

    fun getTasksDueSoon(currentTime: Long, thresholdMillis: Long = 3600000): List<Task> {
        return tasks.filter { it.dueDate != null && it.dueDate!! - currentTime <= thresholdMillis }
    }
}
