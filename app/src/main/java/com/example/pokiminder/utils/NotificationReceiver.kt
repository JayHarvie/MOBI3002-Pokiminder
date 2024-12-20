package com.example.pokiminder.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pokiminder.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderID = intent.getIntExtra("reminderID", 0)
        val title = intent.getStringExtra("title") ?: "Reminder"
        val dueDate = intent.getStringExtra("dueDate") ?: "Unknown Date"

        // Create the notification
        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_notification)  // Set the notification icon
            .setContentTitle("Reminder: $title")
            .setContentText("Due date: $dueDate")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show the notification
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(reminderID, notification)
    }
}

