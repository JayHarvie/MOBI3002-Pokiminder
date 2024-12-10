package com.example.pokiminder.screen

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokiminder.R
import com.example.pokiminder.data.AppDatabase
import com.example.pokiminder.data.entity.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

class CompletedTasksActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var completedTasksRecyclerView: RecyclerView
    private lateinit var completedTasksAdapter: CompletedTasksAdapter
    private var userId: Int? = null  // Store the userId when the user logs in

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_tasks)

        progressBar = findViewById(R.id.progress_bar_completed_tasks)
        completedTasksRecyclerView = findViewById(R.id.recycler_view_completed_tasks)
        completedTasksRecyclerView.layoutManager = GridLayoutManager(this, 2) // Display in a 2-column grid
        completedTasksAdapter = CompletedTasksAdapter()
        completedTasksRecyclerView.adapter = completedTasksAdapter

        // Get the user ID (passed from HomePageActivity)
        userId = intent.getIntExtra("userID", -1)
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if user is not logged in
            return
        }

        // Fetch completed reminders
        fetchCompletedReminders()
    }

    private fun fetchCompletedReminders() {
        val db = AppDatabase.getDatabase(this)
        val reminderDao = db.reminderDao()

        // Show the progress bar before fetching data
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Fetch completed reminders from the database
                val reminders = reminderDao.getCompletedRemindersForUser(userId!!)
                withContext(Dispatchers.Main) {
                    // Log the number of completed reminders
                    Log.d("CompletedTasks", "Fetched ${reminders.size} completed reminders.")

                    // Hide the progress bar after data is fetched
                    progressBar.visibility = View.GONE
                    completedTasksAdapter.submitList(reminders)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Hide the progress bar in case of error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@CompletedTasksActivity, "Error fetching completed reminders", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
