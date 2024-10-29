package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameDifficulty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_difficulty)

        initializeButtons()
    }

    private fun initializeButtons() {
        val easyButton = findViewById<ImageView>(R.id.easy_btn)
        val mediumButton = findViewById<ImageView>(R.id.medium_btn)
        val hardButton = findViewById<ImageView>(R.id.hard_btn)
        val scenario = intent.getStringExtra("selectedScenario") ?: " "

        easyButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.EASY)
            intent.putExtra("selectedScenario", scenario)
        }
        mediumButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.MEDIUM)
            intent.putExtra("selectedScenario", scenario)
        }
        hardButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.HARD)
            intent.putExtra("selectedScenario", scenario)
        }
    }

    private fun startScenarioListWithDifficulty(difficulty: Difficulty) {
        val intent = Intent(this, ScenarioListActivity::class.java)
        intent.putExtra("difficulty", difficulty.name) // Pass the difficulty level as a string
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Start the music again when returning to this activity
        MusicManager.startSound("bg_music")
    }
}
