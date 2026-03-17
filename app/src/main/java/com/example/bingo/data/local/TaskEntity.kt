package com.example.bingo.data.local;

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bingo.domain.TaskType

@Entity(tableName = "tasks")
data class TaskEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val type: TaskType,

    val text: String,

    val isCompleted: Boolean
)