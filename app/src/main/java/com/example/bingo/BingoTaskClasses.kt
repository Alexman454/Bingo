package com.example.bingo.domain

import androidx.compose.runtime.mutableStateListOf

enum class TaskType {
    SIMPLE, ADVANCED, BINGO
}
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
    val size: Int,
    tasks: List<Task>
) {
    val stateGrid: Array<Array<Task>> =
        Array(size) { row ->
            Array(size) { col ->
                tasks[row * size + col]
            }
        }

    fun resetGrid() {
        stateGrid.flatten().forEach { it.isCompleted = false }
    }

    fun completeTaskAt(row: Int, col: Int) {
        stateGrid[row][col].isCompleted = true
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

    fun generateBingo(): BingoTask {
        if (tasks.size < 4)
            throw IllegalStateException("Минимум 4 цели для Bingo")

        val size = kotlin.math.sqrt(tasks.size.toDouble())
            .toInt()
            .coerceIn(2, 5)

        val needed = size * size
        val selected = tasks.shuffled().take(needed)

        return BingoTask(
            size = size,
            tasks = selected
        )
    }


    fun getTasksDueSoon(currentTime: Long, thresholdMillis: Long = 3600000): List<Task> {
        return tasks.filter { it.dueDate != null && it.dueDate!! - currentTime <= thresholdMillis }
    }
}
