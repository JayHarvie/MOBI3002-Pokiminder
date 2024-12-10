package com.example.pokiminder.screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
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
import com.example.pokiminder.utils.getStartAndEndOfMonth
import com.example.pokiminder.utils.getStartAndEndOfWeek
import com.example.pokiminder.utils.getStartAndEndOfYear
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

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

    @RequiresApi(Build.VERSION_CODES.O)
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
        reminderAdapter = ReminderAdapter(
            onCheckboxChanged = { reminder, isChecked -> updateReminderStatus(reminder, isChecked) },
            onReminderClicked = { reminder -> openReminderDetail(reminder) }
        )
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
                        fetchActiveReminders("this_week")
                        Toast.makeText(this, "Due this Week", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.filter_this_month -> {
                        fetchActiveReminders("this_month")
                        Toast.makeText(this, "Due this Month", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.filter_this_year -> {
                        fetchActiveReminders("this_year")
                        Toast.makeText(this, "Due this Year", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.filter_remove -> {
                        fetchActiveReminders(null) // Reset to show all active reminders
                        Toast.makeText(this, "Filter removed", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            // Show the popup menu
            popupMenu.show()
        }

    }

    // Fetch reminders for the month
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchActiveReminders(filter: String? = null) {
        val db = AppDatabase.getDatabase(this)
        val reminderDao = db.reminderDao()

        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val reminders = when (filter) {
                    "this_week" -> {
                        val (startOfWeek, endOfWeek) = getStartAndEndOfWeek()
                        reminderDao.getRemindersDueInRange(
                            userId!!,
                            Date.from(startOfWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            Date.from(endOfWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                        )
                    }
                    "this_month" -> {
                        val (startOfMonth, endOfMonth) = getStartAndEndOfMonth()
                        reminderDao.getRemindersDueInRange(
                            userId!!,
                            Date.from(startOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            Date.from(endOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                        )
                    }
                    "this_year" -> {
                        val (startOfYear, endOfYear) = getStartAndEndOfYear()
                        reminderDao.getRemindersDueInRange(
                            userId!!,
                            Date.from(startOfYear.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            Date.from(endOfYear.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                        )
                    }
                    else -> reminderDao.getActiveRemindersForUser(userId!!)
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    reminderAdapter.submitList(reminders)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@HomePageActivity, "Error fetching reminders", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
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

    private fun openReminderDetail(reminder: Reminder) {
        // Log the dueDate to verify it's passed correctly
        Log.d("HomePageActivity", "Opening reminder detail for reminder with title: ${reminder.title} and dueDate: ${reminder.dueDate}")

        val intent = Intent(this, ReminderDetailActivity::class.java)
        intent.putExtra("reminderId", reminder.reminderID)
        intent.putExtra("title", reminder.title)
        intent.putExtra("dateCompleted", reminder.dueDate.toString())
        intent.putExtra("notes", reminder.notes)
        intent.putExtra("pokemonSprite", reminder.pokemonSprite)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REMINDER_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the reminder list when a reminder is created or deleted
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
        // Clear user session data
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        editor.apply()

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the activity stack
        startActivity(intent)

        // Close this activity
        finish()

        // Show a confirmation Toast (optional)
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

}

