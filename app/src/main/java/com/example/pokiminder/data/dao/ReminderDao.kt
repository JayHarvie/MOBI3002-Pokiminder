package com.example.pokiminder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.pokiminder.data.PokemonDetails
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

    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY dueDate DESC")
    suspend fun getRemindersByUser(userId: Int): List<Reminder>

    @Query("SELECT * FROM reminders WHERE active = 1 ORDER BY dueDate DESC")
    suspend fun getActiveReminders(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE userID = :userId AND active = 1")
    suspend fun getActiveRemindersForUser(userId: Int): List<Reminder>

    @Query("SELECT * FROM reminders WHERE userID = :userId AND active = 0")
    suspend fun getCompletedRemindersForUser(userId: Int): List<Reminder>

    @Query("SELECT pokemonName, pokemonSprite FROM reminders WHERE userId = :userId")
    suspend fun getPokemonDetailsByUserId(userId: Int): List<PokemonDetails>
}
