package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameDifficulty : AppCompatActivity() {

    // onCreate is called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_difficulty) // Set the content view to the activity layout

        // Initialize the buttons for selecting difficulty levels
        initializeButtons()
    }

    // This function initializes button click listeners for difficulty selection
    private fun initializeButtons() {
        // Find the buttons by their IDs in the layout
        val easyButton = findViewById<ImageView>(R.id.easy_btn)
        val mediumButton = findViewById<ImageView>(R.id.medium_btn)
        val hardButton = findViewById<ImageView>(R.id.hard_btn)

        // Get the selected scenario from the Intent, or use a default value
        val scenario = intent.getStringExtra("selectedScenario") ?: " "

        // Set click listener for the Easy button
        easyButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.EASY) // Start the scenario list for easy difficulty
            intent.putExtra("selectedScenario", scenario) // Pass the selected scenario back
        }

        // Set click listener for the Medium button
        mediumButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.MEDIUM) // Start the scenario list for medium difficulty
            intent.putExtra("selectedScenario", scenario) // Pass the selected scenario back
        }

        // Set click listener for the Hard button
        hardButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.HARD) // Start the scenario list for hard difficulty
            intent.putExtra("selectedScenario", scenario) // Pass the selected scenario back
        }
    }

    // This function starts the ScenarioListActivity with the selected difficulty
    private fun startScenarioListWithDifficulty(difficulty: Difficulty) {
        val intent = Intent(this, ScenarioListActivity::class.java)
        intent.putExtra("difficulty", difficulty.name) // Pass the difficulty level as a string
        startActivity(intent) // Start the ScenarioListActivity
    }

    // onResume is called when the activity comes back to the foreground
    override fun onResume() {
        super.onResume()
        // Start the music again when returning to this activity
        MusicManager.startSound("bg_music")
    }
}
