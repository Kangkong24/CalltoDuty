package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameDifficulty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_difficulty)

        val easyButton = findViewById<ImageView>(R.id.easy_btn)
        val mediumButton = findViewById<ImageView>(R.id.medium_btn)
        val hardButton = findViewById<ImageView>(R.id.hard_btn)

        // Set click listeners for each button
        easyButton.setOnClickListener {
            // Start ScenarioListActivity with EASY difficulty
            startScenarioListWithDifficulty(Difficulty.EASY)
        }

        mediumButton.setOnClickListener {
            // Start ScenarioListActivity with MEDIUM difficulty
            startScenarioListWithDifficulty(Difficulty.MEDIUM)
        }

        hardButton.setOnClickListener {
            // Start ScenarioListActivity with HARD difficulty
            startScenarioListWithDifficulty(Difficulty.HARD)
        }
    }

    // Function to start ScenarioListActivity with selected difficulty
    private fun startScenarioListWithDifficulty(difficulty: Difficulty) {
        val intent = Intent(this, ScenarioListActivity::class.java)
        intent.putExtra("difficulty", difficulty.name) // Pass the difficulty level as a string
        startActivity(intent)
    }
}