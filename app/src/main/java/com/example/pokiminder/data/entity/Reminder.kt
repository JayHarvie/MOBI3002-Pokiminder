package com.example.pokiminder.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reminders",
    foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)])
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val reminderID: Int = 0,

    val userId: Int,  // Change this to match the field name in User entity
    val dueDate: Date,
    val title: String,
    val notes: String,
    val numOfReminders: Int,
    val pokemonName: String,
    val pokemonSprite: String,
    val active: Int // true for active reminders, false for completed
)

