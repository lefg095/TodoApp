package com.cursokotlin.todoapp.addtasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    var taskTitle: String,
    var task: String,
    var selected: Boolean = false
)