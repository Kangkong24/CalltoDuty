package com.example.calltoduty

import android.content.Context
import android.widget.Toast
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// Retrofit API service interface for marking and checking scenario completion
interface ApiService {
    @FormUrlEncoded
    @POST("completed_scenario.php") // Endpoint to mark scenario as completed
    fun markScenarioAsCompleted(
        @Field("nickname") nickname: String, // User's nickname
        @Field("scenario_name") scenarioName: String // Name of the scenario
    ): Call<ResponseBody> // Returns a call that provides the server's response

    @FormUrlEncoded
    @POST("is_scenario_completed.php") // Endpoint for checking if the scenario is completed
    fun isScenarioCompleted(
        @Field("nickname") nickname: String, // User's nickname
        @Field("scenario_name") scenarioName: String // Name of the scenario
    ): Call<ResponseBody> // Returns a call that provides the server's response
}

// Class to manage game progress, including marking and checking scenario completion
class GameProgressManager(private val context: Context, private val apiService: ApiService) {
    // SharedPreferences to save and retrieve local progress
    private val sharedPreferences = context.getSharedPreferences("GameProgress", Context.MODE_PRIVATE)

    // Method to mark a scenario as completed both locally and on the server
    fun markScenarioAsCompleted(nickname: String, scenarioName: String) {
        // Create a unique key by combining nickname and scenario name
        val key = "${nickname}_$scenarioName"  // Add nickname as a prefix
        sharedPreferences.edit().putBoolean(key, true).apply() // Save completion state locally

        // Make a network call to mark the scenario as completed on the server
        apiService.markScenarioAsCompleted(nickname, scenarioName).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {  // Check if server response is successful
                    val responseString = response.body()?.string()
                    val jsonResponse = JSONObject(responseString ?: "{}") // Parse response to JSON
                    val status = jsonResponse.optString("status") // Extract status from response
                    when (status) {
                        "completed_scenario_saved" -> {
                            Toast.makeText(context, "Scenario marked as completed", Toast.LENGTH_SHORT).show() // Success message
                        }
                        "already_completed" -> {
                            Toast.makeText(context, "Scenario was already completed", Toast.LENGTH_SHORT).show() // Already completed message
                        }
                        else -> {
                            Toast.makeText(context, "Failed to mark scenario as completed", Toast.LENGTH_SHORT).show() // Failure message
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to mark scenario as completed", Toast.LENGTH_SHORT).show()// Failure message
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show() // Network error message
            }
        })
    }

    // Method to check if a scenario is completed both locally and on the server
    fun isScenarioCompleted(nickname: String, scenarioName: String, callback: (Boolean) -> Unit) {
        // Create a unique key by combining nickname and scenario name
        val key = "${nickname}_$scenarioName"  // Use nickname-specific key
        // Check local completion state
        val completed = sharedPreferences.getBoolean(key, false)
        if (completed) {
            callback(true) // If completed locally, invoke callback with true
        } else {
            // Make a network call to check if the scenario is completed on the server
            apiService.isScenarioCompleted(nickname, scenarioName).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) { // Check if server response is successful
                        val responseString = response.body()?.string()
                        val jsonResponse = JSONObject(responseString ?: "{}") // Parse response to JSON
                        val status = jsonResponse.optString("status") // Extract status from response
                        if (status == "already_completed") {
                            markScenarioAsCompleted(nickname, scenarioName) // Mark scenario as completed locally if found on server
                            callback(true) // Invoke callback with true
                        } else {
                            callback(false) // Invoke callback with false
                        }
                    } else {
                        callback(false) // Invoke callback with false
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()// Network error message
                    callback(false) // Invoke callback with false in case of failure
                }
            })
        }
    }
}
