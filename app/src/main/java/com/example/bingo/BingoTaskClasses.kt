package com.example.bingo.domain

data class Task(
    val id: Int,
    val text: String,
    var isCompleted: Boolean = false,
    var dueDate: Long? = null
)

open class SimpleTask(val task: Task) {
    open fun complete() {
        task.isCompleted = true
    }
    open fun reset() {
        task.isCompleted = false
    }
    open fun info(): String {
        return "${task.id}: ${task.text} [${if (task.isCompleted) "✓" else " "}]"
    }
}

class AdvancedTask(val tasks: MutableList<Task> = mutableListOf()) : SimpleTask(Task(0, "Advanced Task Placeholder")) {
    fun addTask(task: Task) {
        tasks.add(task)
    }
    fun removeTask(id: Int) {
        tasks.removeAll { it.id == id }
    }
    override fun complete() {
        tasks.forEach { it.isCompleted = true }
    }
    override fun reset() {
        tasks.forEach { it.isCompleted = false }
    }
    fun getAllTasks(): List<Task> = tasks.toList()
}

class BingoTask(val bingoGrid: Array<Array<Task>>) : SimpleTask(Task(0, "Bingo Task Placeholder")) {
    fun completeTaskAt(row: Int, col: Int) {
        bingoGrid[row][col].isCompleted = true
    }
    fun resetGrid() {
        for (row in bingoGrid) {
            for (task in row) {
                task.isCompleted = false
            }
        }
    }
    fun printGrid() {
        for (row in bingoGrid) {
            println(row.joinToString(" | ") { if (it.isCompleted) "✓ ${it.text}" else it.text })
        }
    }
}

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
