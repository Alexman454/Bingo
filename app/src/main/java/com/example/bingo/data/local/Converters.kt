package com.example.bingo.data.local

import androidx.room.TypeConverter
import com.example.bingo.domain.TaskType

class Converters {

    @TypeConverter
    fun fromTaskType(value: TaskType): String = value.name

    @TypeConverter
    fun toTaskType(value: String): TaskType =
        TaskType.valueOf(value)
}