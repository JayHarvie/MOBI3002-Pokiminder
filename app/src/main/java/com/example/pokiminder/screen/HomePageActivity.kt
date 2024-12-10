package com.example.pokiminder.screen

import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
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
import com.example.pokiminder.utils.ReminderAdapter
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

    companion object {
        const val REMINDER_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Initialize the ProgressBar and other views
        progressBar = findViewById(R.id.homepage_progress_bar)

        // Initialize the dropdown menu (Spinner)
        dropdownMenu = findViewById(R.id.homepage_dropdown_menu)
        val options = arrayOf("Select an option", "Completed Tasks", "Light Mode", "Dark Mode", "Logout")
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

        dropdownMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
            ) {
                // Handle item selection
                if (position == 0) {
                    // Do nothing when "Select an option" is chosen
                    return
                }

                when (position) {
                    1 -> showCompletedTasks() // Show Completed Tasks
                    2 -> switchTheme(false) // Light Mode
                    3 -> switchTheme(true)  // Dark Mode
                    4 -> logoutUser()       // Logout
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
        reminderAdapter = ReminderAdapter { reminder, isChecked -> updateReminderStatus(reminder, isChecked) }
        reminderRecyclerView.adapter = reminderAdapter

        // Fetch active reminders for the logged-in user
        fetchActiveReminders()

        // Set up the "Create New Reminder" button click listener
        createReminderButton = findViewById(R.id.homepage_create_reminder_button)
        createReminderButton.setOnClickListener {
            val intent = Intent(this, CreateReminderActivity::class.java)
            intent.putExtra("userID", userId)  // Pass the user ID
            startActivityForResult(intent, REMINDER_REQUEST_CODE)  // Start CreateReminderActivity
        }

        // Set up the "Filter" button to show a PopupMenu
        val filterButton: Button = findViewById(R.id.homepage_filter_button)
        filterButton.setOnClickListener {
            // Create a PopupMenu and associate it with the Filter button
            val popupMenu = PopupMenu(this, filterButton)

            // Inflate the menu resource (filter_menu.xml)
            val menuInflater: MenuInflater = menuInflater
            menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

            // Set up the item click listener for the PopupMenu
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.filter_this_week -> {
                        // Handle "Due This Week"
                        Toast.makeText(this, "Due this Week", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.filter_this_month -> {
                        // Handle "Due This Month"
                        Toast.makeText(this, "Due this Month", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.filter_this_year -> {
                        // Handle "Due This Year"
                        Toast.makeText(this, "Due this Year", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            // Show the popup menu
            popupMenu.show()
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

    private fun updateReminderStatus(reminder: Reminder, isChecked: Boolean) {
        val db = AppDatabase.getDatabase(this)
        val reminderDao = db.reminderDao()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                reminder.active = if (isChecked) 0 else 1 // Mark as completed if checked
                reminderDao.updateReminder(reminder) // Update in the database
                withContext(Dispatchers.Main) {
                    // Optionally refresh the list to reflect changes
                    fetchActiveReminders()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomePageActivity, "Error updating reminder status", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REMINDER_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the reminder list when a new reminder is created
            fetchActiveReminders()
        }
    }

    private fun switchTheme(isDarkMode: Boolean) {
        // Switch between Light and Dark modes
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putBoolean("dark_mode", isDarkMode)
        editor.apply()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun showCompletedTasks() {
        // Launch the CompletedTasksActivity
        val intent = Intent(this, CompletedTasksActivity::class.java)
        intent.putExtra("userID", userId)  // Pass the user ID
        startActivity(intent)
    }




    private fun logoutUser() {
        // Handle logout
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
    }
}

