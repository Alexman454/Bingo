package com.example.bingo.domain

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