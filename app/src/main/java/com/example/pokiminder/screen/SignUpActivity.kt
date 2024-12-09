package com.example.pokiminder.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pokiminder.R
import com.example.pokiminder.data.AppDatabase
import com.example.pokiminder.data.dao.UserDao
import com.example.pokiminder.data.entity.User
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var backToLoginButton: Button
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize UI components
        emailEditText = findViewById(R.id.sign_up_email)
        passwordEditText = findViewById(R.id.sign_up_password)
        firstNameEditText = findViewById(R.id.sign_up_first_name)
        lastNameEditText = findViewById(R.id.sign_up_last_name)
        signUpButton = findViewById(R.id.sign_up_button)
        backToLoginButton = findViewById(R.id.back_to_login_button)

        // Initialize Room Database
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the email already exists
            lifecycleScope.launch {
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser != null) {
                    Toast.makeText(this@SignUpActivity, "Email already exists. Please login.", Toast.LENGTH_SHORT).show()
                } else {
                    // Add new user to the database
                    val newUser = User(email = email, password = password, firstName = firstName, lastName = lastName, modeType = 0)
                    userDao.insertUser(newUser)

                    // Redirect to Login page after successful sign up
                    val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Close the sign-up screen
                }
            }
        }

        backToLoginButton.setOnClickListener {
            // Redirect to Login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
