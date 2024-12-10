package com.example.pokiminder.screen

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

    fun submitList(list: List<Reminder>) {
        Log.d("CompletedTasks", "Submitting list with ${list.size} items.")
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
            Log.d("CompletedTasks", "Binding reminder: ${reminder.title}")
            binding.reminder = reminder
            Glide.with(binding.pokemonImage.context)
                .load(reminder.pokemonSprite)
                .placeholder(R.drawable.pokemon_default)
                .error(R.drawable.pokemon_default)
                .into(binding.pokemonImage)
            binding.executePendingBindings()
        }

    }
}
