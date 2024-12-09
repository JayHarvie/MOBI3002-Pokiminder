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


class HomePageActivity : AppCompatActivity() {

    private lateinit var dropdownMenu: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Optional: For edge-to-edge display (if needed)
        setContentView(R.layout.activity_homepage)

        // Set up the layout to handle system bars (for edge-to-edge display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the dropdown menu (Spinner)
        dropdownMenu = findViewById(R.id.homepage_dropdown_menu)

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
        // Show completed tasks here (you might show a list or navigate to another activity)
        Toast.makeText(this, "Showing Completed Tasks", Toast.LENGTH_SHORT).show()
    }
}
