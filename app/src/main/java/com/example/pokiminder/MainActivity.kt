package com.example.pokiminder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.pokiminder.screen.LoginActivity // Ensure correct import for LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content view to the splash screen layout
        setContentView(R.layout.activity_splash)

        // Use a Handler to introduce a delay of 3 seconds before navigating to LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Finish MainActivity so user cannot go back to it
            finish()
        }, 5000) // Delay in milliseconds (3000ms = 3 seconds)
    }
}
