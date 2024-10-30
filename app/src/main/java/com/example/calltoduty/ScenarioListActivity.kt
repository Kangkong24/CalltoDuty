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

class ScenarioListActivity : AppCompatActivity() {
    private lateinit var gameProgressManager: GameProgressManager // Manager to handle game progress
    private lateinit var recyclerView: RecyclerView // RecyclerView to display the list of scenarios
    private lateinit var scenarioAdapter: ScenarioAdapter // Adapter for RecyclerView to bind scenario data
    private lateinit var scenarios: List<EmergencyScenario> // List to hold emergency scenarios

    // onCreate is called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scenario_list) // Set the layout for the activity

        // Retrieve nickname from shared preferences
        val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
        val nickname = sharedPreferences.getString("nickname", "") ?: ""
        Log.d("retrieveNickname", "Nickname retrieved in ScenarioListActivity: $nickname")

        // Initialize views, game progress manager, load scenarios, and setup RecyclerView
        initViews()
        setupGameProgressManager()
        loadScenarios()
        setupRecyclerView()
        unlockScenariosProgressively(nickname)
    }

    // Initialize UI components
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewScenarios) // Find the RecyclerView by ID
        recyclerView.layoutManager = LinearLayoutManager(this) // Set layout manager for RecyclerView
    }

    // Setup Retrofit and GameProgressManager for API calls
    private fun setupGameProgressManager() {
        val gson: Gson = GsonBuilder()
            .setLenient() // Allow lenient parsing of JSON
            .create()

        // Build Retrofit instance
        val apiService = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Base URL for the API
            .addConverterFactory(GsonConverterFactory.create(gson)) // Add Gson converter for JSON parsing
            .build()
            .create(ApiService::class.java) // Create API service instance

        // Initialize GameProgressManager with context and API service
        gameProgressManager = GameProgressManager(this, apiService)
    }

    // Load scenarios based on selected difficulty
    private fun loadScenarios() {
        val difficulty = Difficulty.valueOf(intent.getStringExtra("difficulty")!!) // Get difficulty from intent
        scenarios = getScenariosByDifficulty(difficulty) // Retrieve scenarios by difficulty level
    }

    // Setup RecyclerView with ScenarioAdapter
    private fun setupRecyclerView() {
        scenarioAdapter = ScenarioAdapter(scenarios) { scenario ->
            if (scenario.isUnlocked) {
                startGamePlayActivity(scenario) // Start gameplay if the scenario is unlocked
            }
        }
        recyclerView.adapter = scenarioAdapter // Set the adapter for the RecyclerView
    }

    // Start GamePlay activity with selected scenario
    private fun startGamePlayActivity(scenario: EmergencyScenario) {
        val intent = Intent(this, GamePlay::class.java)
        intent.putExtra("selectedScenario", scenario) // Pass the selected scenario to GamePlay
        startActivity(intent) // Start GamePlay activity
    }

    // Unlock scenarios progressively based on user's completed scenarios
    private fun unlockScenariosProgressively(nickname: String) {
        var completedChecks = 0 // Counter for completed checks

        // Iterate through scenarios to unlock them
        scenarios.forEachIndexed { index, scenario ->
            val previousScenario = if (index > 0) scenarios[index - 1] else null
            if (previousScenario == null) {
                // Unlock the first scenario by default
                scenario.isUnlocked = true
                completedChecks++
            } else {
                // Check if the previous scenario was completed on the server
                gameProgressManager.isScenarioCompleted(nickname, previousScenario.scenarioName) { isCompleted ->
                    scenario.isUnlocked = isCompleted // Update the unlock status based on completion
                    completedChecks++

                    // Save scenario unlock status in shared preferences
                    val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean(scenario.scenarioName, isCompleted) // Save the scenario's unlock status
                        apply() // Apply changes
                    }

                    // Notify the adapter of item change to refresh its view
                    scenarioAdapter.notifyItemChanged(index)

                    // Once all checks are complete, refresh the entire RecyclerView
                    if (completedChecks == scenarios.size) {
                        scenarioAdapter.notifyDataSetChanged() // Notify adapter to refresh all items
                    }
                }
            }
        }
    }

    // Retrieve scenarios based on the specified difficulty
    private fun getScenariosByDifficulty(difficulty: Difficulty): List<EmergencyScenario> {
        return emergencyScenarios.filter { it.difficulty == difficulty } // Filter scenarios by difficulty
    }

    // onResume is called when the activity is back in the foreground
    override fun onResume() {
        super.onResume()
        // Start the background music again when returning to this activity
        MusicManager.startSound("bg_music")
    }
}
