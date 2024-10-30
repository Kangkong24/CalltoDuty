package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// ApiServiceUpdate defines the structure of the HTTP POST request for updating the nickname
// It sends the old and new nicknames to the server's update endpoint
interface ApiServiceUpdate {
    @FormUrlEncoded
    @POST("update.php") // Endpoint for updating the nickname in the database
    fun updateNickname(
        @Field("old_nickname") oldNickname: String, // The current nickname in the database
        @Field("new_nickname") newNickname: String // The new nickname that the user wants to set
    ): Call<ResponseBody>
}

// Main activity class for changing the user nickname
// This class handles updating the user nickname by validating input and sending it to the server
class ChangeNicknamePage : AppCompatActivity() {

    // UI components for close button, nickname input field, and change button
    private lateinit var closeButton: ImageView
    private lateinit var nicknameInput: EditText
    private lateinit var changeButton: ImageView

    // onCreate initializes the activity, retrieves the old nickname, and sets up button actions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nickname_page) // Sets the layout for this activity

        // Initialize UI components for closing the activity, entering a nickname, and changing the nickname
        closeButton = findViewById(R.id.closeButton) // Close button to exit the change page
        nicknameInput = findViewById(R.id.nicknameInput) // Input field for the new nickname
        changeButton = findViewById(R.id.changeButton) // Button to submit the nickname change request

        // Retrieve the old nickname passed from the previous activity
        val oldNickname = intent.getStringExtra("currentNickname") ?: ""
        val signUpNN = intent.getStringExtra("signUp_nickname") ?: ""

        // Set action for the close button to exit the page and return to the previous activity
        closeButton.setOnClickListener {
            finish() // Close the activity and go back to the previous screen
        }

        // Set action for the change button to validate input and initiate nickname update
        changeButton.setOnClickListener {
            val newNickname = nicknameInput.text.toString().trim() // Retrieve and trim the new nickname input

            // Validate that the new nickname is not empty and does not contain spaces
            if (newNickname.isNotEmpty() && !newNickname.contains(" ")) {
                updateNickname(oldNickname, newNickname) // Call updateNickname with the current and new nickname
                updateNickname(signUpNN, newNickname) // Additional call to handle signup nickname if needed
            } else {
                // Show an error if the new nickname is empty or contains spaces
                Toast.makeText(this, "New nickname cannot be empty or contain spaces", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // updateNickname sends a request to the server to change the user's nickname
    // It uses Retrofit to handle the network request and provide callbacks for the response
    private fun updateNickname(oldNickname: String, newNickname: String) {
        // Configure Retrofit instance to make a network request to the server's update endpoint
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.61/rest_api") // Base URL for the server
            .addConverterFactory(GsonConverterFactory.create()) // Converter for parsing JSON responses
            .build()

        // Create an instance of ApiServiceUpdate for making the nickname update request
        val apiService = retrofit.create(ApiServiceUpdate::class.java)

        // Make an asynchronous request to update the nickname on the server
        apiService.updateNickname(oldNickname, newNickname).enqueue(object : Callback<ResponseBody> {

            // onResponse is called when the server responds to the nickname update request
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Check if the server response indicates a successful update
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()?.trim() // Retrieve and trim the response body
                    Log.d("ChangeNicknamePage", "Response Body: $responseBody") // Log the response for debugging

                    // Display appropriate messages and take action based on the server's response
                    if (responseBody == "Nickname updated successfully") {
                        // Notify user of successful nickname update
                        Toast.makeText(this@ChangeNicknamePage, "Nickname updated", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // Show an error message if the server indicates the update failed
                        Toast.makeText(this@ChangeNicknamePage, "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // onFailure is called if the network request fails (e.g., due to connectivity issues)
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Display an error message with details about the failure
                Toast.makeText(this@ChangeNicknamePage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
