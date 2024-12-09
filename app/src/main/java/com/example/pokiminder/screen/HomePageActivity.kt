package com.example.pokiminder.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import android.widget.AdapterView
import com.example.pokiminder.R
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.PopupMenu

class HomePageActivity : AppCompatActivity() {
    private lateinit var dropdownMenu: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        Log.d("HomePageActivity", "onCreate started")

        // Set up the layout to handle system bars (for edge-to-edge display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d("HomePageActivity", "Layout configured")

        // Initialize the dropdown menu (Spinner)
        dropdownMenu = findViewById(R.id.homepage_dropdown_menu)

        Log.d("HomePageActivity", "Dropdown initialized")

        // Set up the dropdown options
        val options = arrayOf("Completed Tasks", "Light Mode", "Dark Mode")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        dropdownMenu.adapter = adapter

        // Set the default theme based on saved preference
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
                    1 -> switchTheme(false) // Light Mode
                    2 -> switchTheme(true)  // Dark Mode
                    else -> showCompletedTasks() // Show Completed Tasks
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected
            }
        }

        Log.d("HomePageActivity", "Dropdown item listener set")

        // Set up the Filter button click listener
        val filterButton = findViewById<Button>(R.id.homepage_filter_button)
        filterButton.setOnClickListener {
            // Show filter options when the button is clicked
            showFilterOptions(filterButton)
        }
    }

    private fun switchTheme(isDarkMode: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putBoolean("dark_mode", isDarkMode).apply()

        // Switch to the selected theme
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun showCompletedTasks() {
        // Handle showing completed tasks here
        Toast.makeText(this, "Showing Completed Tasks", Toast.LENGTH_SHORT).show()
    }

    private fun showFilterOptions(view: android.view.View) {
        // Create a PopupMenu for the filter options
        val popupMenu = PopupMenu(this, view)

        // Inflate the menu from a resource file
        popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

        // Set item click listener for each menu item
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_this_week -> {
                    Toast.makeText(this, "Filtering: Due This Week", Toast.LENGTH_SHORT).show()
                    // Add logic to filter reminders for this week
                    true
                }
                R.id.filter_this_month -> {
                    Toast.makeText(this, "Filtering: Due This Month", Toast.LENGTH_SHORT).show()
                    // Add logic to filter reminders for this month
                    true
                }
                R.id.filter_this_year -> {
                    Toast.makeText(this, "Filtering: Due This Year", Toast.LENGTH_SHORT).show()
                    // Add logic to filter reminders for this year
                    true
                }
                else -> false
            }
        }

        // Show the menu
        popupMenu.show()
    }
}
