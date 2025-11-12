package com.example.bingo.domain

data class Task(
    val id: Int,
    val text: String,
    var isCompleted: Boolean = false
)

class BingoManager {
    private val tasks = mutableListOf<Task>()
    private var id = 1

    fun addTask(text: String) {
        if (text.isBlank()) return
        tasks.add(Task(id = id++, text = text.trim()))
    }

    fun removeTask(id: Int) {
        tasks.removeAll { it.id == id }
    }

    fun getAllTasks(): List<Task> = tasks.toList()

    fun generateBingoGrid(): List<Task> {
        if (tasks.size < 9) {
            throw IllegalStateException("Недостаточно задач: нужно минимум 9")
        }
        return tasks.shuffled().take(9)
    }

    fun completeTask(id: Int) {
        tasks.find { it.id == id }?.isCompleted = true
    }

    fun resetCompletion() {
        tasks.forEach { it.isCompleted = false }
    }
}

fun main() {
    val bingo = BingoManager()

    bingo.addTask("Пробежка")
    bingo.addTask("Убрать комнату")
    bingo.addTask("Постирать")
    bingo.addTask("Почистить ванну")
    bingo.addTask("Выкинуть мусор")
    bingo.addTask("Выгулять собаку")
    bingo.addTask("Сделать домашнее задание")
    bingo.addTask("Написать отчет")
    bingo.addTask("Поесть")

    val grid = bingo.generateBingoGrid()
    println(grid.map { it.text })
    bingo.completeTask(grid.first().id)

}