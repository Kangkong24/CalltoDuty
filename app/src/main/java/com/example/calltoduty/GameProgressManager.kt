package com.example.calltoduty

import android.content.Context

class GameProgressManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("GameProgress", Context.MODE_PRIVATE)

    // Save scenario completion
    fun markScenarioAsCompleted(scenarioName: String) {
        sharedPreferences.edit().putBoolean(scenarioName, true).apply()
    }

    // Check if a scenario is completed
    fun isScenarioCompleted(scenarioName: String): Boolean {
        return sharedPreferences.getBoolean(scenarioName, false)
    }

    // Check if a scenario is unlocked (first scenario is always unlocked)
    fun isScenarioUnlocked(scenarioName: String, previousScenarioName: String?): Boolean {
        return if (previousScenarioName == null) {
            true // First scenario is always unlocked
        } else {
            isScenarioCompleted(previousScenarioName)
        }
    }

    // Reset all progress
    fun resetProgress() {
        sharedPreferences.edit().clear().apply()
    }

    // Save additional information
    fun saveScenarioDetails(scenarioName: String, score: Int, completionTime: Long) {
        sharedPreferences.edit()
            .putInt("${scenarioName}_score", score)
            .putLong("${scenarioName}_completionTime", completionTime)
            .apply()
    }

    // Get scenario score
    fun getScenarioScore(scenarioName: String): Int {
        return sharedPreferences.getInt("${scenarioName}_score", 0)
    }

    // Get scenario completion time
    fun getScenarioCompletionTime(scenarioName: String): Long {
        return sharedPreferences.getLong("${scenarioName}_completionTime", 0L)
    }
}
