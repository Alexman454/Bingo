package com.example.bingo.domain

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