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

        // Rest of your setup code
        initViews()
        setupGameProgressManager()
        loadScenarios()
        setupRecyclerView()
        unlockScenariosProgressively(nickname)
        findViewById<Button>(R.id.reset_btn).setOnClickListener {
            resetProgress()
        }
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
        startActivity(intent)
    }

    private fun unlockScenariosProgressively(nickname: String) {
        for (i in scenarios.indices) {
            val scenario = scenarios[i]
            val previousScenario = if (i > 0) scenarios[i - 1] else null
            if (previousScenario == null) {
                scenario.isUnlocked = true // Unlock the first scenario by default
            } else {
                gameProgressManager.isScenarioCompleted(nickname, previousScenario.scenarioName) { isCompleted ->
                    scenario.isUnlocked = isCompleted
                    scenarioAdapter.notifyItemChanged(i) // Notify RecyclerView of changes
                }
            }
        }
    }

    private fun getScenariosByDifficulty(difficulty: Difficulty): List<EmergencyScenario> {
        return emergencyScenarios.filter { it.difficulty == difficulty }
    }

    private fun resetProgress() {
        gameProgressManager.resetProgress()
        showMessage("All scenarios have been reset.")
        val nickname = intent.getStringExtra("nickname") ?: ""
        unlockScenariosProgressively(nickname)
        scenarioAdapter.notifyDataSetChanged()
    }

    private fun showMessage(message: String) {
        // Display a message to the user
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.startSound("bg_music")
    }
}
