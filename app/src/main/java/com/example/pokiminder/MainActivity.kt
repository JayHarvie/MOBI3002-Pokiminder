package com.example.pokiminder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper // Import Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pokiminder.screen.HomePageActivity // Ensure correct import

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content view to the splash screen layout
        setContentView(R.layout.activity_splash)

        // Optional: For edge-to-edge display (if needed)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Delay for 3 seconds and then navigate to HomePageActivity
        // Updated Handler usage with Looper.getMainLooper()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish() // Finish MainActivity so user cannot go back to it
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}
