package com.example.pokiminder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.pokiminder.data.entity.Reminder

@Dao
interface ReminderDao {

    @Insert
    suspend fun insertReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE reminderId = :id")
    suspend fun getReminderById(id: Int): Reminder?

    @Query("SELECT * FROM reminders WHERE userId = :userId")
    suspend fun getRemindersByUser(userId: Int): List<Reminder>

    @Query("SELECT * FROM reminders WHERE active = 1")
    suspend fun getActiveReminders(): List<Reminder>
}
