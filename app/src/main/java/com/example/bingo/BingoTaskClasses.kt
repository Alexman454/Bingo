package com.example.bingo.domain

import androidx.compose.runtime.mutableStateListOf

/**
 * Тип задачи.
 */
enum class TaskType {
    SIMPLE,
    ADVANCED,
    BINGO
}

/**
 * Абстрактный класс для отображаемых задач.
 */
sealed class DisplayableTask {
    data class Simple(val simpleTask: SimpleTask) : DisplayableTask()
    data class Advanced(val advancedTask: AdvancedTask) : DisplayableTask()
    data class Bingo(val bingoTask: BingoTask) : DisplayableTask()
}

/**
 * Основная модель задачи.
 *
 * @property id уникальный идентификатор задачи.
 * @property text текст задачи.
 * @property isCompleted статус выполнения задачи.
 * @property dueDate дата дедлайна задачи в миллисекундах (nullable).
 */
data class Task(
    val id: Int,
    val text: String,
    var isCompleted: Boolean = false,
    var dueDate: Long? = null
)

/**
 * Простая задача.
 *
 * @property task базовая задача.
 */
data class SimpleTask(val task: Task) {

    /** Отметить задачу как выполненную. */
    fun complete() {
        task.isCompleted = true
    }

    /** Сбросить статус выполнения задачи. */
    fun reset() {
        task.isCompleted = false
    }

    /** Получить информацию о задаче в формате строки. */
    fun info(): String = "${task.id}: ${task.text} [${if (task.isCompleted) "✓" else " "}]"
}

/**
 * Продвинутая задача с подзадачами.
 *
 * @property tasks список подзадач.
 */
class AdvancedTask(
    val tasks: MutableList<Task> = mutableListOf()
) {
    /** Состояние задач для Compose UI. */
    var stateTasks = mutableStateListOf<Task>().apply { addAll(tasks) }

    /**
     * Добавить подзадачу.
     *
     * @param task задача для добавления.
     */
    fun addTask(task: Task) {
        tasks.add(task)
        stateTasks.add(task)
    }

    /**
     * Удалить подзадачу по идентификатору.
     *
     * @param id идентификатор задачи для удаления.
     */
    fun removeTask(id: Int) {
        tasks.removeAll { it.id == id }
        stateTasks.removeAll { it.id == id }
    }

    /** Отметить все подзадачи как выполненные. */
    fun complete() {
        tasks.forEach { it.isCompleted = true }
        stateTasks.forEach { it.isCompleted = true }
    }

    /** Сбросить выполнение всех подзадач. */
    fun reset() {
        tasks.forEach { it.isCompleted = false }
        stateTasks.forEach { it.isCompleted = false }
    }

    /** Получить список всех подзадач. */
    fun getAllTasks(): List<Task> = tasks
}

/**
 * Бинго-сетка.
 *
 * @property size размер сетки (NxN).
 * @property stateGrid двумерный массив задач.
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

    /** Сбросить выполнение всех задач в сетке. */
    fun resetGrid() {
        stateGrid.flatten().forEach { it.isCompleted = false }
    }

    /**
     * Отметить задачу в сетке как выполненную.
     *
     * @param row номер строки.
     * @param col номер столбца.
     */
    fun completeTaskAt(row: Int, col: Int) {
        stateGrid[row][col].isCompleted = true
    }

    /** Вывести сетку бинго в консоль. */
    fun printGrid() {
        for (row in stateGrid) {
            println(row.joinToString(" | ") { if (it.isCompleted) "✓ ${it.text}" else it.text })
        }
    }
}

/**
 * Класс для управления списком задач и формирования бинго-сетки.
 */
class BingoManager {
    private val tasks = mutableListOf<Task>()
    private var idCounter = 1

    /**
     * Добавить новую задачу.
     *
     * @param text текст задачи.
     * @param dueDate дата дедлайна (optional).
     */
    fun addTask(text: String, dueDate: Long? = null) {
        if (text.isBlank()) return
        tasks.add(Task(id = idCounter++, text = text.trim(), dueDate = dueDate))
    }

    /**
     * Удалить задачу по идентификатору.
     *
     * @param id идентификатор задачи.
     */
    fun removeTask(id: Int) {
        tasks.removeAll { it.id == id }
    }

    /** Получить список всех задач. */
    fun getAllTasks(): List<Task> = tasks.toList()

    /**
     * Отметить задачу как выполненную.
     *
     * @param id идентификатор задачи.
     */
    fun completeTask(id: Int) {
        tasks.find { it.id == id }?.isCompleted = true
    }

    /** Сбросить выполнение всех задач. */
    fun resetCompletion() {
        tasks.forEach { it.isCompleted = false }
    }

    /**
     * Сгенерировать бинго-сетку.
     *
     * @throws IllegalStateException если задач меньше 4.
     * @return объект BingoTask.
     */
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

    /**
     * Получить задачи, срок выполнения которых скоро наступит.
     *
     * @param currentTime текущее время в миллисекундах.
     * @param thresholdMillis порог времени до дедлайна (по умолчанию 1 час).
     * @return список задач с близким дедлайном.
     */
    fun getTasksDueSoon(currentTime: Long, thresholdMillis: Long = 3600000): List<Task> {
        return tasks.filter { it.dueDate != null && it.dueDate!! - currentTime <= thresholdMillis }
    }
}
