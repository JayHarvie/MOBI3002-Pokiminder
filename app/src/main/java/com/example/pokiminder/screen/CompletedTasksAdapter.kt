package com.example.pokiminder.screen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokiminder.R
import com.example.pokiminder.data.entity.Reminder
import com.example.pokiminder.databinding.ItemCompletedReminderBinding

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
            // Load the Pokémon sprite image using Glide from the URL stored in pokemonSprite
            Glide.with(binding.pokemonImage.context)
                .load(reminder.pokemonSprite)  // pokemonSprite is the URL of the image
                .placeholder(R.drawable.pokemon_default)  // Placeholder while the image loads
                .error(R.drawable.pokemon_default)  // Fallback image in case of an error
                .into(binding.pokemonImage)
            binding.executePendingBindings()
        }
    }
}
