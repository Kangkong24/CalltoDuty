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

// Retrofit API service interface
interface ApiService {
    @FormUrlEncoded
    @POST("completed_scenario.php") // Endpoint for marking scenario as completed
    fun markScenarioAsCompleted(
        @Field("nickname") nickname: String,
        @Field("scenario_name") scenarioName: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("completed_scenario.php") // Endpoint for checking if the scenario is completed
    fun isScenarioCompleted(
        @Field("nickname") nickname: String,
        @Field("scenario_name") scenarioName: String
    ): Call<ResponseBody>
}

// GameProgressManager class to handle game progress
class GameProgressManager(private val context: Context, private val apiService: ApiService) {
    private val sharedPreferences = context.getSharedPreferences("GameProgress", Context.MODE_PRIVATE)

    // Save scenario completion locally and mark it on the server
    fun markScenarioAsCompleted(nickname: String, scenarioName: String) {
        // Save the completion state locally
        val key = "${nickname}_$scenarioName"  // Add nickname as a prefix
        sharedPreferences.edit().putBoolean(key, true).apply()

        // Make network call to mark it on the server
        apiService.markScenarioAsCompleted(nickname, scenarioName).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseString = response.body()?.string()
                    val jsonResponse = JSONObject(responseString ?: "{}")
                    val status = jsonResponse.optString("status")

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

    // Check if a scenario is completed locally and from the server
    fun isScenarioCompleted(nickname: String, scenarioName: String, callback: (Boolean) -> Unit) {
        // Check local progress first
        val key = "${nickname}_$scenarioName"  // Use nickname-specific key
        val completed = sharedPreferences.getBoolean(key, false)
        if (completed) {
            callback(true) // Already completed locally
        } else {
            // Make a network call to check if it's completed on the server
            apiService.isScenarioCompleted(nickname, scenarioName).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseString = response.body()?.string()
                        val jsonResponse = JSONObject(responseString ?: "{}")
                        val status = jsonResponse.optString("status")

                        if (status == "already_completed") {
                            markScenarioAsCompleted(nickname, scenarioName)
                            callback(true) // Mark as completed if found on server
                        } else {
                            callback(false) // Not completed
                        }
                    } else {
                        callback(false) // Not completed
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    callback(false) // Assume not completed if there's a failure
                }
            })
        }
    }

    // Reset all progress
    fun resetProgress(nickname: String) {
        val editor = sharedPreferences.edit()
        sharedPreferences.all.keys
            .filter { it.startsWith("${nickname}_") }  // Only remove keys for the given nickname
            .forEach { editor.remove(it) }
        editor.apply()
    }


}
