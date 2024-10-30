package com.example.calltoduty

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Activity that displays a splash screen with an animated progress bar
class SplashScreen : AppCompatActivity() {
    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display
        setContentView(R.layout.activity_splash_screen) // Set the content view

        // Initialize the progress bar and start the animation
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val animationDrawable = progressBar.progressDrawable as AnimationDrawable
        animationDrawable.start() // Start the progress bar animation

        // Delay the transition to the next activity for 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SignUpScreen::class.java) // Create an intent to start SignUpScreen
            startActivity(intent) // Start the SignUpScreen activity
            finish() // Finish the SplashScreen activity
        }, 5000) // 5000 milliseconds delay

        // Set up window insets to adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // Get insets for system bars
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // Set padding
            insets // Return the insets
        }
    }
}
