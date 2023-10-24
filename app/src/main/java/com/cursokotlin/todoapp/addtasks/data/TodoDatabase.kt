package com.cursokotlin.todoapp.addtasks.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 2)
abstract class TodoDatabase:RoomDatabase() {
    abstract fun taskDao():TaskDao
}