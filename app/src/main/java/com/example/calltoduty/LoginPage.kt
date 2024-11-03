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

// Interface for defining the API service to handle login requests
interface ApiServiceLogin {
    @FormUrlEncoded
    @POST("login.php") // Endpoint for the login request
    fun login(@Field("nickname") nickname: String): Call<ResponseBody> // Call object for handling the server response
}

// Activity class for the login page
class LoginPage : AppCompatActivity() {

    // Declare UI elements: an EditText for nickname input and a Button to submit the login request
    private lateinit var nickNameInput: EditText
    private lateinit var enterButton: Button

    // Function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page) // Set the layout for this activity

        nickNameInput = findViewById(R.id.nn_input) // Initialize the nickname input field
        enterButton = findViewById(R.id.enterButton) // Initialize the enter button

        // Set up the enter button to handle the login request
        enterButton.setOnClickListener {
            val nickname = nickNameInput.text.toString().trim() // Get and trim the input text

            // Check if nickname is not empty and doesn't contain spaces
            if (nickname.isNotEmpty() && !nickname.contains(" ")) {
                checkLogin(nickname) // Call the function to check the login
            } else {
                // Show a message if the input is invalid
                Toast.makeText(this, "Nickname cannot be empty or contain spaces", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to check the login by making a network request
    private fun checkLogin(nickname: String) {
        // Set up Retrofit for making the HTTP request
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Base URL of the server; should be your device's IP
            .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter for JSON
            .build()
        val apiService = retrofit.create(ApiServiceLogin::class.java) // Create an instance of the API service
        Log.d("LoginAttempt", "Attempting to login with nickname: $nickname")

        // Make the network call to check the login
        apiService.login(nickname).enqueue(object : Callback<ResponseBody> {
            // Called when the server responds
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Response Code", "Response Code: ${response.code()}") // Log response code
                if (response.isSuccessful) { // If the response is successful
                    val responseBody = response.body()?.string() // Get the response body as a string
                    Log.d("LoginResponse", responseBody ?: "null")
                    if (responseBody != null) {
                        val trimmedResponse = responseBody.trim() // Trim the response string
                        Log.d("Trimmed Response", trimmedResponse)
                        // Handle the different possible responses
                        when (trimmedResponse) {
                            "Login successful" -> {
                                Toast.makeText(this@LoginPage, "Hi, $nickname!", Toast.LENGTH_SHORT).show()// Show a success message
                                // Save the nickname to SharedPreferences
                                val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
                                sharedPreferences.edit().putString("nickname", nickname).apply()
                                Log.d("saveNickname", "Nickname saved: $nickname")

                                val intent = Intent(this@LoginPage, MainActivity::class.java)
                                intent.putExtra("currentNickname", nickname) // Pass the nickname to the main activity
                                startActivity(intent)  // Start the main activity
                            }
                            "User not found" -> {
                                Toast.makeText(this@LoginPage, "Nickname not found", Toast.LENGTH_SHORT).show() // Show a user not found message
                            }
                            "No progress found for user" -> {
                                Toast.makeText(this@LoginPage, "No progress found", Toast.LENGTH_SHORT).show() // Show a no progress message
                            }
                            "Nickname cannot be empty or contain spaces" -> {
                                Toast.makeText(this@LoginPage, "Invalid nickname", Toast.LENGTH_SHORT).show() // Show an invalid nickname message
                            }
                            else -> {
                                Toast.makeText(this@LoginPage, "Unknown response from server", Toast.LENGTH_SHORT).show() // Show an unknown response message
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginPage, "Empty response from server", Toast.LENGTH_SHORT).show()// Show an empty response message
                    }
                } else {
                    Toast.makeText(this@LoginPage, "Error: ${response.message()}", Toast.LENGTH_SHORT).show() // Show an error message with the response message
                }
            }

            // Called if the network request fails
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Show an error message with the throwable message
                Toast.makeText(this@LoginPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
