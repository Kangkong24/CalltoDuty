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

// Defines the Retrofit API service for deleting an account
interface ApiServiceDelete {
    // Specifies a POST request to "delete.php" for account deletion
    @FormUrlEncoded
    @POST("delete.php")
    fun deleteAccount(@Field("nickname") nickname: String): Call<ResponseBody>
}

class DeletionPage : AppCompatActivity() {

    // Declares buttons for "Yes" (to confirm deletion) and "No" (to cancel)
    private lateinit var yesButton: Button
    private lateinit var noButton: Button

    // Initializes the activity layout and button functionality
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deletion_page)

        // Finds the buttons from the XML layout
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)

        // Retrieves nicknames from previous activities passed via Intent
        val oldNickname = intent.getStringExtra("currentNickname") ?: ""
        val signUpNN = intent.getStringExtra("signUp_nickname") ?: ""
        val updatedNickname = intent.getStringExtra("updatedNickname") ?: ""

        // Sets an action for the "No" button to close the current activity and return
        noButton.setOnClickListener {
            finish() // Closes the current activity and returns to the previous screen
        }

        // Sets an action for the "Yes" button to initiate account deletion based on available nickname
        yesButton.setOnClickListener {
            // Selects the correct nickname based on intent values to delete the account
            when {
                oldNickname.isNotEmpty() -> deleteAccount(oldNickname)
                signUpNN.isNotEmpty() -> deleteAccount(signUpNN)
                updatedNickname.isNotEmpty() -> deleteAccount(updatedNickname)
                else -> Toast.makeText(this, "Nickname is not set.", Toast.LENGTH_SHORT).show() // Error if no nickname is set
            }
        }
    }

    // Sends a request to delete the account from the server
    private fun deleteAccount(nickname: String) {
        // Configures Retrofit instance with base URL and JSON converter
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.61/rest_api") // Replace with the correct server IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creates an API service instance for handling delete requests
        val apiService = retrofit.create(ApiServiceDelete::class.java)

        // Sends a request to the server to delete the account with the specified nickname
        apiService.deleteAccount(nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Checks if the response from the server indicates a successful deletion
                if (response.isSuccessful) {
                    Toast.makeText(this@DeletionPage, "Account deleted", Toast.LENGTH_SHORT).show()

                    // Sets up an Intent to return the user to the SignUpScreen and clears the back stack
                    val intent = Intent(this@DeletionPage, SignUpScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent) // Launches the SignUpScreen activity
                } else {
                    // Displays a failure message if the deletion was unsuccessful
                    Toast.makeText(this@DeletionPage, "Deletion failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handles network or server errors during the deletion process
                Toast.makeText(this@DeletionPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
