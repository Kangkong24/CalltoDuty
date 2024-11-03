package com.example.calltoduty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Activity class for displaying the list of emergency scenarios
class ScenarioListActivity : AppCompatActivity() {
    private lateinit var gameProgressManager: GameProgressManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var scenarioAdapter: ScenarioAdapter
    private lateinit var scenarios: List<EmergencyScenario>

    // Function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scenario_list) // Set the layout for this activity

        // Retrieve the nickname from SharedPreferences
        val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
        val nickname = sharedPreferences.getString("nickname", "") ?: ""
        Log.d("retrieveNickname", "Nickname retrieved in ScenarioListActivity: $nickname")


        initViews() // Initialize the views
        setupGameProgressManager() // Set up the game progress manager
        loadScenarios() // Load the scenarios based on difficulty
        setupRecyclerView() // Set up the RecyclerView
        unlockScenariosProgressively(nickname) // Unlock scenarios progressively

    }

    // Initialize the RecyclerView and set its layout manager
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewScenarios)
        recyclerView.layoutManager = LinearLayoutManager(this) // Use a linear layout manager
    }

    // Set up the game progress manager to handle network requests
   private fun setupGameProgressManager() {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()
        val apiService = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Base URL of the server
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
        gameProgressManager = GameProgressManager(this, apiService)
    }

    // Load scenarios based on the selected difficulty
    private fun loadScenarios() {
        val difficulty = Difficulty.valueOf(intent.getStringExtra("difficulty")!!)
        scenarios = getScenariosByDifficulty(difficulty)
    }

    // Set up the RecyclerView adapter
    private fun setupRecyclerView() {
        scenarioAdapter = ScenarioAdapter(scenarios) { scenario ->
            if (scenario.isUnlocked) {
                startGamePlayActivity(scenario)
            }
        }
        recyclerView.adapter = scenarioAdapter
    }

    // Start the game play activity with the selected scenario
    private fun startGamePlayActivity(scenario: EmergencyScenario) {
        val intent = Intent(this, GamePlay::class.java)
        intent.putExtra("selectedScenario", scenario)
        intent.putExtra("difficulty", scenario.difficulty.name) // Pass difficulty to GamePlay
        startActivity(intent)
    }

    // Unlock scenarios progressively based on game progress
    private fun unlockScenariosProgressively(nickname: String) {
        var completedChecks = 0 // Counter for completed checks

        scenarios.forEachIndexed { index, scenario ->
            val previousScenario = if (index > 0) scenarios[index - 1] else null
            if (previousScenario == null) {
                // Unlock the first scenario by default
                scenario.isUnlocked = true
                completedChecks++
            } else {
                // Check if the previous scenario was completed on the server
                gameProgressManager.isScenarioCompleted(nickname, previousScenario.scenarioName) { isCompleted ->
                    scenario.isUnlocked = isCompleted
                    completedChecks++
                    // Save scenario as unlocked if completed
                    val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean(scenario.scenarioName, isCompleted)
                        apply()
                    }
                    // Update item once unlocked status is changed
                    scenarioAdapter.notifyItemChanged(index)

                    // Once all checks are complete, refresh the entire RecyclerView
                    if (completedChecks == scenarios.size) {
                        scenarioAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }




    // Retrieve scenarios based on the selected difficulty
    private fun getScenariosByDifficulty(difficulty: Difficulty): List<EmergencyScenario> {
        return emergencyScenarios.filter { it.difficulty == difficulty }
    }

    // Called when the activity is resumed, e.g., when returning from another activity
    override fun onResume() {
        super.onResume()
        MusicManager.startSound("bg_music") // Start the background music
    }
}
