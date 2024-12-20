package com.example.pokiminder.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pokiminder.data.entity.Reminder
import com.example.pokiminder.databinding.ItemReminderBinding

// Adapter to handle list of reminders
class ReminderAdapter(
    private val onCheckboxChanged: (Reminder, Boolean) -> Unit,
    private val onReminderClicked: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding, onCheckboxChanged, onReminderClicked)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.bind(reminder)
    }

    // ViewHolder for individual reminder items
    class ReminderViewHolder(
        private val binding: ItemReminderBinding,
        private val onCheckboxChanged: (Reminder, Boolean) -> Unit,
        private val onReminderClicked: (Reminder) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Set up the listener for the checkbox
            binding.checkboxComplete.setOnCheckedChangeListener { _, isChecked ->
                val reminder = binding.reminder
                reminder?.let {
                    onCheckboxChanged(it, isChecked) // Trigger the callback for checkbox changes
                }
            }

            // Set up the listener for the title click
            binding.reminderTitle.setOnClickListener {
                val reminder = binding.reminder
                reminder?.let {
                    onReminderClicked(it) // Trigger the callback for title clicks
                }
            }
        }

        fun bind(reminder: Reminder) {
            binding.reminder = reminder
            binding.executePendingBindings()
        }
    }
}

// DiffUtil callback for comparing reminders
class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        // Compare the unique identifiers (e.g., ReminderID)
        return oldItem.reminderID == newItem.reminderID
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        // Compare all fields to determine if contents are the same
        return oldItem == newItem
    }
}
