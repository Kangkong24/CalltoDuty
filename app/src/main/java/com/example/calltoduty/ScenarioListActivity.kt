package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScenarioListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scenarioAdapter: ScenarioAdapter
    private lateinit var scenarios: List<EmergencyScenario>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scenario_list)

        recyclerView = findViewById(R.id.recyclerViewScenarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Retrieve the difficulty from the Intent
        val difficultyString = intent.getStringExtra("difficulty")
        val selectedDifficulty = Difficulty.valueOf(difficultyString!!)

        // Get the scenarios for the selected difficulty
        scenarios = getScenariosByDifficulty(selectedDifficulty)

        // Set up the adapter with the filtered scenarios
        scenarioAdapter = ScenarioAdapter(scenarios) { scenario ->
            // Handle scenario selection
            val intent = Intent(this, GamePlay::class.java)
            intent.putExtra("selectedScenario", scenario)
            startActivity(intent)
        }
        recyclerView.adapter = scenarioAdapter
    }

    private fun getScenariosByDifficulty(difficulty: Difficulty): List<EmergencyScenario> {
        return emergencyScenarios.filter { it.difficulty == difficulty }
    }

    override fun onResume() {
        super.onResume()
        //Start the music again when returning to this activity
        MusicManager.startSound("bg_music")
    }

}