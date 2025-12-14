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

/**
 * Класс, управляющий списком задач и формированием бинго-сетки и
 * отвечающий за добавление, удаление, завершение задач, генерацию бинго-сетки
 * и получение задач с приближающимся сроком выполнения.
 */
class BingoManager {
    private val tasks = mutableListOf<Task>()
    private var idCounter = 1

    /**
     * Добавляет новую задачу в список.
     * @param text Текст задачи. Если пустой или состоит только из пробелов, задача не добавляется.
     * @param dueDate Срок выполнения задачи.
     */
    fun addTask(text: String, dueDate: Long? = null) {
        if (text.isBlank()) return
        tasks.add(Task(id = idCounter++, text = text.trim(), dueDate = dueDate))
    }

    /**
     * Удаляет задачу в зависимости от идентификатора.
     * @param id Идентификатор задачи для удаления.
     */
    fun removeTask(id: Int) {
        tasks.removeAll { it.id == id }
    }

    /**
     * Возвращает список всех задач.
     *
     * @return Копия списка всех задач.
     */
    fun getAllTasks(): List<Task> = tasks.toList()

    /**
     * Помечает задачу с указанным идентификатором как выполненную.
     *
     * @param id Идентификатор задачи для завершения.
     */
    fun completeTask(id: Int) {
        tasks.find { it.id == id }?.isCompleted = true
    }

    /**
     * Сбрасывает статус выполнения всех задач.
     */
    fun resetCompletion() {
        tasks.forEach { it.isCompleted = false }
    }

    /**
     * Генерирует бинго-сетку размером 3x3 на основе текущего списка задач.
     *
     * @return Двумерный массив задач размером 3x3.
     * @throws IllegalStateException Если в списке меньше 9 задач.
     */
    fun generateBingoGrid(): Array<Array<Task>> {
        if (tasks.size < 9) throw IllegalStateException("Недостаточно задач: нужно минимум 9")
        val selected = tasks.shuffled().take(9)
        return Array(3) { row ->
            Array(3) { col ->
                selected[row * 3 + col]
            }
        }
    }

    /**
     * Возвращает список задач, у которых срок выполнения наступает в ближайшее время.
     *
     * @param currentTime Текущее время в миллисекундах.
     * @param thresholdMillis Пороговое время в миллисекундах для определения "срочных" задач (по умолчанию 1 час).
     * @return Список задач, срок выполнения которых наступает в пределах thresholdMillis от текущего времени.
     */
    fun getTasksDueSoon(currentTime: Long, thresholdMillis: Long = 3600000): List<Task> {
        return tasks.filter { it.dueDate != null && it.dueDate!! - currentTime <= thresholdMillis }
    }
}
