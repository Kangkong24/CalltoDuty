package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

// Activity class for selecting the game difficulty level
class GameDifficulty : AppCompatActivity() {

    // Function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_difficulty) // Set the layout for this activity

        initializeButtons() // Initialize the buttons for selecting difficulty levels
    }

    // Function to set up the buttons and their click listeners
    private fun initializeButtons() {
        // Find the button views by their IDs
        val easyButton = findViewById<ImageView>(R.id.easy_btn)
        val mediumButton = findViewById<ImageView>(R.id.medium_btn)
        val hardButton = findViewById<ImageView>(R.id.hard_btn)
        val scenario = intent.getStringExtra("selectedScenario") ?: " " // Get the selected scenario from the intent

        // Set click listeners for each button to start the scenario list with the selected difficulty
        easyButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.EASY)
            intent.putExtra("selectedScenario", scenario) // Pass the selected scenario back to the intent
        }
        mediumButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.MEDIUM)
            intent.putExtra("selectedScenario", scenario) // Pass the selected scenario back to the intent
        }
        hardButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.HARD)
            intent.putExtra("selectedScenario", scenario) // Pass the selected scenario back to the intent
        }
    }

    // Function to start the ScenarioListActivity with the chosen difficulty level
    private fun startScenarioListWithDifficulty(difficulty: Difficulty) {
        val intent = Intent(this, ScenarioListActivity::class.java)
        intent.putExtra("difficulty", difficulty.name) // Pass the difficulty level as a string
        startActivity(intent) // Start the new activity
    }

    // Called when the activity is resumed, e.g., when returning from another activity
    override fun onResume() {
        super.onResume()
        // Start the music again when returning to this activity
        MusicManager.startSound("bg_music")
    }
}
