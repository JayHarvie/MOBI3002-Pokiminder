package com.example.pokiminder.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pokiminder.utils.NotificationReceiver

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
    var active: Int // true for active reminders, false for completed
)

fun Reminder.scheduleNotifications(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Calculate the dates when notifications should be triggered
    val dueCalendar = Calendar.getInstance().apply {
        time = dueDate
    }

    val notificationTimes = mutableListOf<Calendar>()

    when (numOfReminders) {
        1 -> notificationTimes.add(Calendar.getInstance().apply {
            time = dueDate
            add(Calendar.DAY_OF_YEAR, -1)  // 1 day before due date
        })
        2 -> {
            notificationTimes.add(Calendar.getInstance().apply {
                time = dueDate
                add(Calendar.DAY_OF_YEAR, -2)  // 2 days before due date
            })
            notificationTimes.add(Calendar.getInstance().apply {
                time = dueDate
                add(Calendar.DAY_OF_YEAR, -1)  // 1 day before due date
            })
        }
        3 -> {
            notificationTimes.add(Calendar.getInstance().apply {
                time = dueDate
                add(Calendar.DAY_OF_YEAR, -3)  // 3 days before due date
            })
            notificationTimes.add(Calendar.getInstance().apply {
                time = dueDate
                add(Calendar.DAY_OF_YEAR, -2)  // 2 days before due date
            })
            notificationTimes.add(Calendar.getInstance().apply {
                time = dueDate
                add(Calendar.DAY_OF_YEAR, -1)  // 1 day before due date
            })
        }
    }

    // Schedule notifications for each time in notificationTimes
    notificationTimes.forEachIndexed { index, calendar ->
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("reminderID", reminderID)
            putExtra("title", title)
            putExtra("dueDate", dueDate.toString())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderID * 100 + index, // Unique request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}

