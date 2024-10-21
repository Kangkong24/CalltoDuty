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

        easyButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.EASY)
        }
        mediumButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.MEDIUM)
        }
        hardButton.setOnClickListener {
            startScenarioListWithDifficulty(Difficulty.HARD)
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
