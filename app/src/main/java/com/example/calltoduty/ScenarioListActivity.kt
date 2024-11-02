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
    private lateinit var gameProgressManager: GameProgressManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var scenarioAdapter: ScenarioAdapter
    private lateinit var scenarios: List<EmergencyScenario>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scenario_list)

        val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
        val nickname = sharedPreferences.getString("nickname", "") ?: ""
        Log.d("retrieveNickname", "Nickname retrieved in ScenarioListActivity: $nickname")


        initViews()
        setupGameProgressManager()
        loadScenarios()
        setupRecyclerView()
        unlockScenariosProgressively(nickname)

    }


    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewScenarios)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

   private fun setupGameProgressManager() {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()
        val apiService = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
        gameProgressManager = GameProgressManager(this, apiService)
    }

    private fun loadScenarios() {
        val difficulty = Difficulty.valueOf(intent.getStringExtra("difficulty")!!)
        scenarios = getScenariosByDifficulty(difficulty)
    }

    private fun setupRecyclerView() {
        scenarioAdapter = ScenarioAdapter(scenarios) { scenario ->
            if (scenario.isUnlocked) {
                startGamePlayActivity(scenario)
            }
        }
        recyclerView.adapter = scenarioAdapter
    }

    private fun startGamePlayActivity(scenario: EmergencyScenario) {
        val intent = Intent(this, GamePlay::class.java)
        intent.putExtra("selectedScenario", scenario)
        intent.putExtra("difficulty", scenario.difficulty.name) // Pass difficulty to GamePlay
        startActivity(intent)
    }

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





    private fun getScenariosByDifficulty(difficulty: Difficulty): List<EmergencyScenario> {
        return emergencyScenarios.filter { it.difficulty == difficulty }
    }

    override fun onResume() {
        super.onResume()
        MusicManager.startSound("bg_music")
    }
}
