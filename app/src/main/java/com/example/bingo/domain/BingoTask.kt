package com.example.bingo.domain

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