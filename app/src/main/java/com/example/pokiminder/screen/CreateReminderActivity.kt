package com.example.pokiminder.screen

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pokiminder.R
import com.example.pokiminder.data.AppDatabase
import com.example.pokiminder.data.entity.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import android.util.Log

class CreateReminderActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var notesEditText: EditText
    private lateinit var selectDateButton: Button
    private lateinit var numRemindersSpinner: Spinner
    private lateinit var createReminderButton: Button
    private var selectedDate: Date? = null
    private var userId: Int = 0 // Variable to store the userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content view to the create reminder layout
        setContentView(R.layout.activity_create_reminder)

        // Get userId from intent (assuming userId is passed when navigating to this activity)
        userId = intent.getIntExtra("userID", 0) // Corrected parameter name to 'userId'
        Log.d("CreateReminderActivity", "User ID: $userId")

        titleEditText = findViewById(R.id.editText_title)
        notesEditText = findViewById(R.id.editText_notes)
        selectDateButton = findViewById(R.id.button_select_date)
        numRemindersSpinner = findViewById(R.id.spinner_num_reminders)
        createReminderButton = findViewById(R.id.button_create_reminder)

        // Populate spinner with options (1, 2, 3)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(1, 2, 3))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        numRemindersSpinner.adapter = spinnerAdapter

        // Select date button logic
        selectDateButton.setOnClickListener {
            showDatePicker()
        }

        // Create reminder button logic
        createReminderButton.setOnClickListener {
            createReminder()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = selectedCalendar.time
            selectDateButton.text = selectedDate.toString()
        }, year, month, day).show()
    }

    private fun createReminder() {
        val title = titleEditText.text.toString()
        val notes = notesEditText.text.toString()
        val numOfReminders = numRemindersSpinner.selectedItem as Int

        if (title.isBlank() || notes.isBlank() || selectedDate == null) {
            Toast.makeText(this, "Please fill out all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch a random Pokémon
        CoroutineScope(Dispatchers.IO).launch {
            val pokemonId = Random.nextInt(1, 152) // Random number between 1 and 151
            val pokemon = fetchPokemonData(pokemonId)

            // Use userId passed from the previous activity

            val reminder = Reminder(
                userId = userId,
                dueDate = selectedDate!!,
                title = title,
                notes = notes,
                numOfReminders = numOfReminders,
                pokemonName = pokemon.first,
                pokemonSprite = pokemon.second,
                active = 1
            )

            // Save reminder to the database
            val database = AppDatabase.getDatabase(applicationContext)
            database.reminderDao().insertReminder(reminder)

            runOnUiThread {
                Toast.makeText(this@CreateReminderActivity, "Reminder created!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            }
        }
    }

    private fun fetchPokemonData(pokemonId: Int): Pair<String, String> {
        val url = "https://pokeapi.co/api/v2/pokemon-form/$pokemonId/"

        // Create an OkHttpClient instance
        val client = OkHttpClient()

        // Build the HTTP request
        val request = Request.Builder()
            .url(url)
            .build()

        // Execute the request
        val response = client.newCall(request).execute()

        // Ensure the response is successful
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch Pokémon data: ${response.code}")
        }

        // Parse the JSON response
        val json = JSONObject(response.body?.string() ?: throw Exception("Response body is null"))
        val pokemonName = json.getString("name")
        val spriteUrl = json.getJSONObject("sprites").getString("front_default")

        return Pair(pokemonName, spriteUrl)
    }
}
