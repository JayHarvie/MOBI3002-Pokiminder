package com.example.pokiminder.screen

import android.annotation.SuppressLint
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
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var userDao: UserDao

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI components
        emailEditText = findViewById(R.id.login_email)
        passwordEditText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        signUpButton = findViewById(R.id.sign_up_button)

        // Initialize Room Database
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check user credentials in the database
            lifecycleScope.launch {
                val user = userDao.getUserByEmailAndPassword(email, password)
                if (user != null) {
                    // Successful login, redirect to HomePageActivity
                    val intent = Intent(this@LoginActivity, HomePageActivity::class.java)
                    startActivity(intent)
                    finish() // Close the login screen
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid credentials, please try again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        signUpButton.setOnClickListener {
            // Redirect to Sign-Up screen
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
