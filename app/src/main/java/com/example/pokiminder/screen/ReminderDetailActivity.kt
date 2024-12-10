package com.example.pokiminder.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.pokiminder.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.pokiminder.data.AppDatabase
import android.widget.Toast

class ReminderDetailActivity : AppCompatActivity() {

    private var reminderId: Int = -1  // Declare a variable to hold the reminder ID

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_detail)

        // Set up the toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_reminder_detail)
        setSupportActionBar(toolbar)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reminder Details"

        // Get data passed from the intent
        val title = intent.getStringExtra("title")
        val dateCompleted = intent.getStringExtra("dateCompleted")
        val notes = intent.getStringExtra("notes")
        val pokemonSprite = intent.getStringExtra("pokemonSprite")

        // Retrieve the reminder ID passed with the intent
        reminderId = intent.getIntExtra("reminderId", -1)

        // Initialize views
        val titleTextView: TextView = findViewById(R.id.textView_title)
        val dateTextView: TextView = findViewById(R.id.textView_date_completed)
        val notesTextView: TextView = findViewById(R.id.textView_notes)
        val pokemonImageView: ImageView = findViewById(R.id.imageView_pokemon_sprite)

        // Set data to views
        titleTextView.text = title
        dateTextView.text = dateCompleted
        notesTextView.text = notes

        // Load Pokémon sprite
        Glide.with(this)
            .load(pokemonSprite)
            .placeholder(R.drawable.pokemon_default)
            .error(R.drawable.pokemon_default)
            .into(pokemonImageView)

        // Set up the delete button
        val deleteButton: Button = findViewById(R.id.button_delete_reminder)
        deleteButton.setOnClickListener {
            // Show a confirmation dialog or directly delete the reminder
            deleteReminder(reminderId)
        }
    }

    // Delete the reminder
    private fun deleteReminder(reminderId: Int) {
        if (reminderId != -1) {
            val db = AppDatabase.getDatabase(this)
            val reminderDao = db.reminderDao()

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Fetch the reminder by ID and delete it from the database
                    val reminder = reminderDao.getReminderById(reminderId)
                    if (reminder != null) {
                        reminderDao.deleteReminder(reminder) // Delete the reminder
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ReminderDetailActivity, "Reminder deleted", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish() // Close the activity
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ReminderDetailActivity, "Reminder not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ReminderDetailActivity, "Error deleting reminder", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Invalid reminder ID", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the back button press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish() // Close the activity and go back to the previous screen
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
