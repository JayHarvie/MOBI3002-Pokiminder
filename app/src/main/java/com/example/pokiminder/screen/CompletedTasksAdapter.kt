package com.example.pokiminder.screen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokiminder.R
import com.example.pokiminder.data.entity.Reminder
import com.example.pokiminder.databinding.ItemCompletedReminderBinding
import android.util.Log

class CompletedTasksAdapter : RecyclerView.Adapter<CompletedTasksAdapter.CompletedTasksViewHolder>() {

    private var completedTasks = listOf<Reminder>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Reminder>) {
        Log.d("CompletedTasks", "Submitting list with ${list.size} items.")
        completedTasks = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTasksViewHolder {
        Log.d("CompletedTasks", "onCreateViewHolder called")
        val binding = ItemCompletedReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("CompletedTasks", "Binding object created: $binding")
        return CompletedTasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompletedTasksViewHolder, position: Int) {
        Log.d("CompletedTasks", "onBindViewHolder called for position: $position")
        val reminder = completedTasks[position]
        holder.bind(reminder)
    }

    override fun getItemCount(): Int = completedTasks.size

    class CompletedTasksViewHolder(private val binding: ItemCompletedReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder) {
            Log.d("CompletedTasks", "Binding reminder: ${reminder.title}, Pokemon Sprite: ${reminder.pokemonSprite}")
            binding.reminder = reminder

            // Load the Pokemon sprite using Glide
            Glide.with(binding.pokemonImage.context)
                .load(reminder.pokemonSprite)
                .placeholder(R.drawable.pokemon_default)
                .error(R.drawable.pokemon_default)
                .into(binding.pokemonImage)

            Log.d("CompletedTasks", "Pokemon image loaded for ${reminder.title}")
            binding.executePendingBindings()  // Ensure that the data binding is triggered
        }
    }
}

