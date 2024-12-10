package com.example.pokiminder.data.dao

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.pokiminder.data.entity.Reminder
import com.example.pokiminder.utils.getStartAndEndOfWeek
import com.example.pokiminder.utils.getStartAndEndOfMonth
import com.example.pokiminder.utils.getStartAndEndOfYear
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

@Dao
interface ReminderDao {

    // Insert a new reminder
    @Insert
    suspend fun insertReminder(reminder: Reminder)

    // Update an existing reminder
    @Update
    suspend fun updateReminder(reminder: Reminder)

    // Delete a reminder
    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    // Get a reminder by its ID
    @Query("SELECT * FROM reminders WHERE reminderId = :id")
    suspend fun getReminderById(id: Int): Reminder?

    // Get all active reminders for a user
    @Query("SELECT * FROM reminders WHERE userId = :userId AND active = 1")
    suspend fun getActiveRemindersForUser(userId: Int): List<Reminder>

    // Get all completed reminders for a user
    @Query("SELECT * FROM reminders WHERE userId = :userId AND active = 0")
    suspend fun getCompletedRemindersForUser(userId: Int): List<Reminder>

    // Get reminders due within a specific date range
    @Query("""
        SELECT * FROM reminders 
        WHERE userId = :userId AND dueDate BETWEEN :startDate AND :endDate AND active = 1
    """)
    suspend fun getRemindersDueInRange(
        userId: Int,
        startDate: Date,
        endDate: Date
    ): List<Reminder>

    // Get reminders due this week
    @Query("""
        SELECT * FROM reminders 
        WHERE userId = :userId AND dueDate BETWEEN :startDate AND :endDate AND active = 1
    """)
    suspend fun getRemindersDueThisWeek(userId: Int, startDate: Date, endDate: Date): List<Reminder>

    // Get reminders due this month
    @Query("""
        SELECT * FROM reminders 
        WHERE userId = :userId AND dueDate BETWEEN :startDate AND :endDate AND active = 1
    """)
    suspend fun getRemindersDueThisMonth(userId: Int, startDate: Date, endDate: Date): List<Reminder>

    // Get reminders due this year
    @Query("""
        SELECT * FROM reminders 
        WHERE userId = :userId AND dueDate BETWEEN :startDate AND :endDate AND active = 1
    """)
    suspend fun getRemindersDueThisYear(userId: Int, startDate: Date, endDate: Date): List<Reminder>
}

