package com.cursokotlin.todoapp.addtasks.ui.model

data class TaskModel(
    val id: Int = System.currentTimeMillis().hashCode(),
    val taskTitle: String,
    val task: String,
    var selected: Boolean = false
)