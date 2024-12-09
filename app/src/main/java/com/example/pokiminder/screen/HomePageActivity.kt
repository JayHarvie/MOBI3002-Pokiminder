package com.example.pokiminder.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokiminder.R
import com.example.pokiminder.data.AppDatabase
import com.example.pokiminder.data.entity.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePageActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var dropdownMenu: Spinner
    private lateinit var createReminderButton: Button
    private lateinit var reminderRecyclerView: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter
    private var userId: Int? = null  // Store the userId when the user logs in

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Initialize the ProgressBar
        progressBar = findViewById(R.id.homepage_progress_bar)

        // Initialize the dropdown menu (Spinner)
        dropdownMenu = findViewById(R.id.homepage_dropdown_menu)
        val options = arrayOf("Completed Tasks", "Light Mode", "Dark Mode", "Logout")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        dropdownMenu.adapter = adapter

        // Set the theme based on saved preference
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val darkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Set item selection listener for the dropdown menu
        dropdownMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
            ) {
                when (position) {
                    0 -> showCompletedTasks() // Show Completed Tasks
                    1 -> switchTheme(false) // Light Mode
                    2 -> switchTheme(true)  // Dark Mode
                    3 -> logoutUser()       // Logout
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected
            }
        }

        // Get the user ID (passed from LoginActivity)
        userId = intent.getIntExtra("userID", -1)
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if user is not logged in
            return
        }

        // Initialize RecyclerView for reminders
        reminderRecyclerView = findViewById(R.id.homepage_reminder_list)
        reminderRecyclerView.layoutManager = LinearLayoutManager(this)
        reminderAdapter = ReminderAdapter()
        reminderRecyclerView.adapter = reminderAdapter

        // Fetch active reminders for the logged-in user
        fetchActiveReminders()

        // Set up the "Create New Reminder" button click listener
        createReminderButton = findViewById(R.id.homepage_create_reminder_button)
        createReminderButton.setOnClickListener {
            val intent = Intent(this, CreateReminderActivity::class.java)
            intent.putExtra("userID", userId)  // Pass the user ID
            startActivity(intent)
        }
    }

    private fun fetchActiveReminders() {
        val db = AppDatabase.getDatabase(this)
        val reminderDao = db.reminderDao()

        // Show the progress bar before fetching data
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Fetch reminders from the database
                val reminders = reminderDao.getActiveRemindersForUser(userId!!)
                withContext(Dispatchers.Main) {
                    // Hide the progress bar after data is fetched
                    progressBar.visibility = View.GONE
                    reminderAdapter.submitList(reminders)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Hide the progress bar in case of error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@HomePageActivity, "Error fetching reminders", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun switchTheme(isDarkMode: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putBoolean("dark_mode", isDarkMode).apply()
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun showCompletedTasks() {
        Toast.makeText(this, "Showing Completed Tasks", Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
