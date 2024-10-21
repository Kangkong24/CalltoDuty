package com.example.calltoduty

import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {
    @FormUrlEncoded
    @POST("completed_scenario.php")
    fun markScenarioAsCompleted(
        @Field("nickname") nickname: String,
        @Field("scenario_name") scenarioName: String
    ): Call<String>

    @FormUrlEncoded
    @POST("completed_scenario.php")
    fun isScenarioCompleted(
        @Field("nickname") nickname: String,
        @Field("scenario_name") scenarioName: String
    ): Call<String>
}


class GameProgressManager(private val context: Context, private val apiService: ApiService) {

    private val sharedPreferences = context.getSharedPreferences("GameProgress", Context.MODE_PRIVATE)

    // Save scenario completion locally and mark it on the server
    fun markScenarioAsCompleted(nickname: String, scenarioName: String) {
        sharedPreferences.edit().putBoolean(scenarioName, true).apply()

        // Make network call to mark it on the server
        apiService.markScenarioAsCompleted(nickname, scenarioName).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Scenario marked as completed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to mark scenario as completed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Check if a scenario is completed locally and from the server
    fun isScenarioCompleted(nickname: String, scenarioName: String, callback: (Boolean) -> Unit) {
        val completed = sharedPreferences.getBoolean(scenarioName, false)
        if (completed) {
            callback(true) // Already completed locally
        } else {
            apiService.isScenarioCompleted(nickname, scenarioName).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful && response.body() == "true") {
                        markScenarioAsCompleted(nickname, scenarioName)
                        callback(true)
                    } else {
                        callback(false)
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    callback(false) // Assume not completed if there's a failure
                }
            })
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
