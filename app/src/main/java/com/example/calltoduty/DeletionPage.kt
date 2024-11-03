package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

// Interface for defining the API service to delete an account
interface ApiServiceDelete {
    // Defines the structure of the POST request with encoded form data
    @FormUrlEncoded
    @POST("delete.php") // URL endpoint for the delete request
    fun deleteAccount(@Field("nickname") nickname: String): Call<ResponseBody> // Call object for handling the server response
}

// Main activity class for deleting a user account
class DeletionPage : AppCompatActivity() {

    // Declare UI elements: buttons for confirming and cancelling the deletion
    private lateinit var yesButton: Button
    private lateinit var noButton: Button

    // Function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deletion_page) // Set the layout for this activity

        // Initialize the UI components by finding them by their IDs
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)

        // Get the nicknames passed from the previous activity
        val oldNickname = intent.getStringExtra("currentNickname") ?: ""
        val signUpNN = intent.getStringExtra("signUp_nickname") ?: ""
        val updatedNickname = intent.getStringExtra("updatedNickname") ?: ""

        // Set up the no button to finish (close) the activity when clicked
        noButton.setOnClickListener {
            finish() // Closes the current activity and returns to the previous one
        }

        // Set up the yes button to handle account deletion
        yesButton.setOnClickListener {
            // Check which nickname to use for the deletion request
            when {
                oldNickname.isNotEmpty() -> deleteAccount(oldNickname)
                signUpNN.isNotEmpty() -> deleteAccount(signUpNN)
                updatedNickname.isNotEmpty() -> deleteAccount(updatedNickname)
                else -> Toast.makeText(this, "Nickname is not set.", Toast.LENGTH_SHORT).show() // Show a message if no nickname is set
            }
        }
    }

    // Function to delete the account by making a network request
    private fun deleteAccount(nickname: String) {
        // Set up Retrofit for making the HTTP request
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Base URL of the server; should be your device's IP
            .addConverterFactory(GsonConverterFactory.create())  // Add Gson converter for JSON
            .build()

        // Create an instance of the API service
        val apiService = retrofit.create(ApiServiceDelete::class.java)

        // Make the network call to delete the account
        apiService.deleteAccount(nickname).enqueue(object : Callback<ResponseBody> {
            // Called when the server responds
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Show a success message
                    Toast.makeText(this@DeletionPage, "Account deleted", Toast.LENGTH_SHORT).show()
                    // Start the SignUpScreen activity and clear the activity stack
                    val intent = Intent(this@DeletionPage, SignUpScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear previous activities
                    startActivity(intent)
                } else {
                    // Show a failure message
                    Toast.makeText(this@DeletionPage, "Deletion failed", Toast.LENGTH_SHORT).show()
                }
            }

            // Called if the network request fails
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Show an error message
                Toast.makeText(this@DeletionPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
