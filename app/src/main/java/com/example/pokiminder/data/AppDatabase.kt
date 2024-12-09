package com.example.pokiminder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pokiminder.data.dao.ReminderDao
import com.example.pokiminder.data.dao.UserDao
import com.example.pokiminder.data.entity.Reminder
import com.example.pokiminder.data.entity.User

@Database(entities = [User::class, Reminder::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class) // Register the Converters
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pokiminder_database"
                )
                    .fallbackToDestructiveMigration() // Handles migration by clearing old data
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
