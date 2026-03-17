package com.example.bingo.domain

/**
 * Продвинутая задача с подзадачами.
 *
 * @property tasks список подзадач.
 */
class AdvancedTask(
    val tasks: MutableList<Task> = mutableListOf()
) {

    /**
     * Добавить подзадачу.
     *
     * @param task задача для добавления.
     */
    fun addTask(task: Task) {
        tasks.add(task)
    }

    /**
     * Удалить подзадачу по идентификатору.
     *
     * @param id идентификатор задачи для удаления.
     */
    fun removeTask(id: Int) {
        tasks.removeAll { it.id == id }
    }

    /** Отметить все подзадачи как выполненные. */
    fun complete() {
        tasks.forEach { it.isCompleted = true }
    }

    /** Сбросить выполнение всех подзадач. */
    fun reset() {
        tasks.forEach { it.isCompleted = false }
    }

    /** Получить список всех подзадач. */
    fun getAllTasks(): List<Task> = tasks
}