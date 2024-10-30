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

// Retrofit API service interface for server communication
interface ApiService {
    @FormUrlEncoded
    @POST("completed_scenario.php") // Endpoint for marking a scenario as completed
    fun markScenarioAsCompleted(
        @Field("nickname") nickname: String, // User's nickname
        @Field("scenario_name") scenarioName: String // Name of the scenario
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("is_scenario_completed.php") // Endpoint for checking if the scenario is completed
    fun isScenarioCompleted(
        @Field("nickname") nickname: String, // User's nickname
        @Field("scenario_name") scenarioName: String // Name of the scenario
    ): Call<ResponseBody>
}

// GameProgressManager class to handle user progress in the game
class GameProgressManager(private val context: Context, private val apiService: ApiService) {
    private val sharedPreferences = context.getSharedPreferences("GameProgress", Context.MODE_PRIVATE)

    // Method to mark a scenario as completed
    fun markScenarioAsCompleted(nickname: String, scenarioName: String) {
        // Save the completion state locally using a unique key
        val key = "${nickname}_$scenarioName"  // Key is a combination of nickname and scenario name
        sharedPreferences.edit().putBoolean(key, true).apply() // Update shared preferences

        // Make a network call to mark the scenario as completed on the server
        apiService.markScenarioAsCompleted(nickname, scenarioName).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseString = response.body()?.string() // Get the response body as a string
                    val jsonResponse = JSONObject(responseString ?: "{}") // Parse the response as JSON
                    val status = jsonResponse.optString("status") // Extract the status from the JSON response
                    when (status) {
                        "completed_scenario_saved" -> {
                            Toast.makeText(context, "Scenario marked as completed", Toast.LENGTH_SHORT).show()
                        }
                        "already_completed" -> {
                            Toast.makeText(context, "Scenario was already completed", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Failed to mark scenario as completed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to mark scenario as completed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Method to check if a scenario is completed
    fun isScenarioCompleted(nickname: String, scenarioName: String, callback: (Boolean) -> Unit) {
        // Check local progress first
        val key = "${nickname}_$scenarioName"  // Key for the user's completion status
        val completed = sharedPreferences.getBoolean(key, false) // Retrieve completion status from shared preferences
        if (completed) {
            callback(true) // Scenario already completed locally
        } else {
            // Make a network call to check if the scenario is completed on the server
            apiService.isScenarioCompleted(nickname, scenarioName).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseString = response.body()?.string() // Get the response body as a string
                        val jsonResponse = JSONObject(responseString ?: "{}") // Parse the response as JSON
                        val status = jsonResponse.optString("status") // Extract the status from the JSON response
                        if (status == "already_completed") {
                            markScenarioAsCompleted(nickname, scenarioName) // Mark as completed if found on server
                            callback(true) // Scenario is completed
                        } else {
                            callback(false) // Scenario is not completed
                        }
                    } else {
                        callback(false) // Scenario is not completed if response is unsuccessful
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    callback(false) // Assume not completed if there's a network failure
                }
            })
        }
    }
}
