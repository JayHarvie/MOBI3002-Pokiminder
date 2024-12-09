package com.example.pokiminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val reminderID: Int = 0,

    val userID: Int,
    val dueDate: Date,
    val title: String,
    val notes: String,
    val numOfReminders: Int,
    val pokemon: String,
    val active: Int // 0 = inactive, 1 = active
)
