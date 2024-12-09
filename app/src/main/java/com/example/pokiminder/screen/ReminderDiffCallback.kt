//package com.example.pokiminder.screen
//
//import androidx.recyclerview.widget.DiffUtil
//import com.example.pokiminder.data.entity.Reminder
//
//class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
//    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
//        // Compare based on the unique identifier (reminderId)
//        return oldItem.reminderID == newItem.reminderID
//    }
//
//    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
//        // Compare all fields to check if the contents are the same
//        return oldItem == newItem
//    }
//}
