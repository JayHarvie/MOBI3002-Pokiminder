package com.example.pokiminder.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokiminder.R
import com.example.pokiminder.data.entity.Reminder
import com.example.pokiminder.databinding.ItemCompletedReminderBinding
import com.example.pokiminder.screen.ReminderDetailActivity

class CompletedTasksAdapter : RecyclerView.Adapter<CompletedTasksAdapter.CompletedTasksViewHolder>() {

    private var completedTasks = listOf<Reminder>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Reminder>) {
        completedTasks = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTasksViewHolder {
        val binding = ItemCompletedReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompletedTasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompletedTasksViewHolder, position: Int) {
        val reminder = completedTasks[position]
        holder.bind(reminder)
    }

    override fun getItemCount(): Int = completedTasks.size

    class CompletedTasksViewHolder(private val binding: ItemCompletedReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder) {
            binding.reminder = reminder

            // Load the Pokémon sprite image
            Glide.with(binding.pokemonImage.context)
                .load(reminder.pokemonSprite)
                .placeholder(R.drawable.pokemon_default)
                .error(R.drawable.pokemon_default)
                .into(binding.pokemonImage)

            // Set click listener
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ReminderDetailActivity::class.java).apply {
                    putExtra("reminderId", reminder.reminderID)
                    putExtra("title", reminder.title)
                    putExtra("dateCompleted", reminder.dueDate.toString()) // Convert Date to String
                    putExtra("notes", reminder.notes)
                    putExtra("pokemonSprite", reminder.pokemonSprite)
                }
                context.startActivity(intent)
            }

            binding.executePendingBindings()
        }
    }
}
