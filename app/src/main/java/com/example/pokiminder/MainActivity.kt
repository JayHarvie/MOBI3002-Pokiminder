package com.example.pokiminder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper // Import Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.pokiminder.screen.HomePageActivity // Ensure correct import

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content view to the splash screen layout
        setContentView(R.layout.activity_splash)

        // Use a Handler to introduce a delay of 3 seconds before navigating to HomePageActivity
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigate to HomePageActivity
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)

            // Finish MainActivity so user cannot go back to it
            finish()
        }, 3000) // Delay in milliseconds (3000ms = 3 seconds)
    }
}
