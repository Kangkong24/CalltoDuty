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
    // Defines the structure of the POST request with encoded form data
    @FormUrlEncoded
    @POST("update.php") //URL Endpoint for updating the nickname in the database
    fun updateNickname(
        @Field("old_nickname") oldNickname: String,
        @Field("new_nickname") newNickname: String
    ): Call<ResponseBody>
}

// Activity class for changing the user nickname
// This class handles updating the user nickname by validating input and sending it to the server
class ChangeNicknamePage : AppCompatActivity() {

    // UI components for close button, nickname input field, and change button
    private lateinit var closeButton: ImageView
    private lateinit var nicknameInput: EditText
    private lateinit var changeButton: ImageView

    // Function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nickname_page) // Set the layout for this activity

        // Initialize the UI components by finding them by their IDs
        closeButton = findViewById(R.id.closeButton)
        nicknameInput = findViewById(R.id.nicknameInput)
        changeButton = findViewById(R.id.changeButton)

        // Get the old nickname passed from the previous activity
        val oldNickname = intent.getStringExtra("currentNickname") ?: ""  // If no value, use an empty string
        val signUpNN = intent.getStringExtra("signUp_nickname") ?: ""   // Another nickname to update



        // Set up the close button to finish (close) the activity when clicked
        closeButton.setOnClickListener {
            finish() // Close the current activity and returns to the previous one
        }

        // Set up the change button to handle the nickname update
        changeButton.setOnClickListener {
            val newNickname = nicknameInput.text.toString().trim() // Get and trim the input text

            // Check if the new nickname is not empty and doesn't contain spaces
            if (newNickname.isNotEmpty() && !newNickname.contains(" ")) {
                // Call the function to update the nickname
                updateNickname(oldNickname, newNickname)
                updateNickname(signUpNN, newNickname)
            } else {
                // Show a message if the input is invalid
                Toast.makeText(this, "New nickname cannot be empty or contain spaces", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to update the nickname by making a network request
    private fun updateNickname(oldNickname: String, newNickname: String) {
        // Set up Retrofit for making the HTTP request
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Base URL of the server; should be your device's IP
            .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter for JSON
            .build()

        // Create an instance of the API service
        val apiService = retrofit.create(ApiServiceUpdate::class.java)

        // Make the network call to update the nickname
        apiService.updateNickname(oldNickname, newNickname).enqueue(object : Callback<ResponseBody> {
            // Called when the server responds
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) { // If the response is successful
                    val responseBody = response.body()?.string()?.trim() // Get the response body and trim it
                    Log.d("ChangeNicknamePage", "Response Body: $responseBody") // Log the response
                    if (responseBody == "Nickname updated successfully") { // Check if the response is a success message
                        // Show a success message
                        Toast.makeText(this@ChangeNicknamePage, "Nickname updated", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity
                    } else {
                        // Show a failure message
                        Toast.makeText(this@ChangeNicknamePage, "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Called if the network request fails
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ChangeNicknamePage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
