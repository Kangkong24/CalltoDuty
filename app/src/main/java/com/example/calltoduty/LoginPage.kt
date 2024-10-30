package com.example.calltoduty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

// Define the API service interface for handling login requests using Retrofit
// This interface manages HTTP POST requests to the server's login endpoint
interface ApiServiceLogin {
    @FormUrlEncoded
    @POST("login.php") // Endpoint for handling login requests
    fun login(@Field("nickname") nickname: String): Call<ResponseBody>
}

// Main activity class for the Login page
// This class handles the login process by verifying the user nickname input and communicating with the server
class LoginPage : AppCompatActivity() {

    // Variables for EditText input field and Button on the Login page
    private lateinit var nickNameInput: EditText
    private lateinit var enterButton: Button

    // onCreate initializes the activity, sets up the UI components, and defines click actions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page) // Set layout file for this activity

        // Initialize the UI components (EditText and Button)
        nickNameInput = findViewById(R.id.nn_input) // Input field for entering the nickname
        enterButton = findViewById(R.id.enterButton) // Button for submitting nickname to login

        // Set click listener for the enter button
        enterButton.setOnClickListener {
            val nickname = nickNameInput.text.toString().trim() // Retrieve and trim nickname input to remove leading/trailing whitespace

            // Validate that nickname is not empty and doesn't contain spaces
            // If validation passes, proceed with the login check
            if (nickname.isNotEmpty() && !nickname.contains(" ")) {
                checkLogin(nickname) // Call checkLogin to authenticate nickname with the server
            } else {
                // Show error message if nickname is empty or contains spaces
                Toast.makeText(this, "Nickname cannot be empty or contain spaces", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // checkLogin handles the authentication process by sending the nickname to the server
    // This function uses Retrofit to make an asynchronous network request to the login endpoint
    private fun checkLogin(nickname: String) {
        // Configure a Retrofit instance to manage network requests
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.61/rest_api") // Base URL of the API endpoint
            .addConverterFactory(GsonConverterFactory.create()) // Converter for parsing JSON responses
            .build()

        // Create an instance of the ApiServiceLogin interface
        val apiService = retrofit.create(ApiServiceLogin::class.java)
        Log.d("LoginAttempt", "Attempting to login with nickname: $nickname") // Log the nickname for debugging purposes

        // Make an asynchronous login request to the server using the nickname
        apiService.login(nickname).enqueue(object : Callback<ResponseBody> {

            // onResponse is called when the server responds to the login request
            // It processes the server's response, handling both successful logins and any error messages
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Response Code", "Response Code: ${response.code()}") // Log the server's response code

                if (response.isSuccessful) { // Check if the response from the server was successful (status code 200)
                    val responseBody = response.body()?.string() // Retrieve the response body as a string
                    Log.d("LoginResponse", responseBody ?: "null") // Log the server's response for debugging

                    if (responseBody != null) { // Verify that the response body is not null
                        val trimmedResponse = responseBody.trim() // Trim whitespace from response for precise comparison
                        Log.d("Trimmed Response", trimmedResponse) // Log trimmed response for debugging

                        // Handle different server responses based on the response content
                        when (trimmedResponse) {
                            "Login successful" -> {
                                // Display welcome message upon successful login
                                Toast.makeText(this@LoginPage, "Hi, $nickname!", Toast.LENGTH_SHORT).show()

                                // Save the nickname in SharedPreferences for later retrieval within the app
                                val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
                                sharedPreferences.edit().putString("nickname", nickname).apply()
                                Log.d("saveNickname", "Nickname saved: $nickname") // Log the saved nickname for debugging

                                // Start MainActivity and pass the nickname to it
                                val intent = Intent(this@LoginPage, MainActivity::class.java)
                                intent.putExtra("currentNickname", nickname) // Pass nickname as an extra to MainActivity
                                startActivity(intent)
                            }
                            "User not found" -> {
                                // Show error message if the user is not found in the database
                                Toast.makeText(this@LoginPage, "Nickname not found", Toast.LENGTH_SHORT).show()
                            }
                            "No progress found for user" -> {
                                // Show message if the user has no recorded progress in the game
                                Toast.makeText(this@LoginPage, "No progress found", Toast.LENGTH_SHORT).show()
                            }
                            "Nickname cannot be empty or contain spaces" -> {
                                // Show error message if the nickname contains invalid characters (should not occur here since input is validated)
                                Toast.makeText(this@LoginPage, "Invalid nickname", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                // Display a message for any unexpected or unknown server response
                                Toast.makeText(this@LoginPage, "Unknown response from server", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Show a message if the server response body is empty
                        Toast.makeText(this@LoginPage, "Empty response from server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Display an error message if the response status is not successful
                    Toast.makeText(this@LoginPage, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            // onFailure is called if the network request fails due to connectivity issues or server errors
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Display error message with details of the failure
                Toast.makeText(this@LoginPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
