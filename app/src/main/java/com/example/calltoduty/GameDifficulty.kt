package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameDifficulty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_difficulty)

        // Find the ImageView buttons
        val easyButton = findViewById<ImageView>(R.id.easy_btn)
        val mediumButton = findViewById<ImageView>(R.id.medium_btn)
        val hardButton = findViewById<ImageView>(R.id.hard_btn)

        // Set click listeners for each button
        easyButton.setOnClickListener {
            // Start GameActivity with EASY difficulty
            startGameWithDifficulty(Difficulty.EASY)
        }

        mediumButton.setOnClickListener {
            // Start GameActivity with MEDIUM difficulty
            startGameWithDifficulty(Difficulty.MEDIUM)
        }

        hardButton.setOnClickListener {
            // Start GameActivity with HARD difficulty
            startGameWithDifficulty(Difficulty.HARD)
        }
    }

    // Function to start GameActivity with selected difficulty
    private fun startGameWithDifficulty(difficulty: Difficulty) {
        val intent = Intent(this, GamePlay::class.java)
        intent.putExtra("difficulty", difficulty.name) // Pass the difficulty level as a string
        startActivity(intent)
    }
}