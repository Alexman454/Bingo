package com.example.bingo.domain

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
