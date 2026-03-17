package com.example.bingo.data.repository

import com.example.bingo.data.local.TaskDao
import com.example.bingo.data.local.TaskEntity
import com.example.bingo.domain.TaskType
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val dao: TaskDao
) {

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return dao.getAll()
    }

    suspend fun addSimpleTask(text: String) {
        val entity = TaskEntity(
            type = TaskType.SIMPLE,
            text = text,
            isCompleted = false
        )
        dao.insert(entity)
    }

    suspend fun deleteTask(task: TaskEntity) {
        dao.delete(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        dao.update(task)
    }
}