package com.example.bingo.domain

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