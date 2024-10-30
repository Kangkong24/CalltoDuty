package com.example.calltoduty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// Define the interface for the signup API service using Retrofit
// This interface manages HTTP POST requests to the signup endpoint on the server
interface ApiServiceSignUp {
    @FormUrlEncoded
    @POST("signup.php") // Define the endpoint for the signup action
    fun signup(
        @Field("nickname") nickname: String // Pass the nickname field to the server
    ): Call<ResponseBody> // Define the call type as ResponseBody for server responses
}

// Main activity class for the Sign-Up screen
class SignUpScreen : AppCompatActivity() {

    private lateinit var nickNameInput: EditText // EditText for user input for nickname
    private lateinit var createBtn: Button // Button for creating a new account
    private lateinit var alreadyTv: TextView // TextView for navigating to the login screen

    // onCreate: initializes the activity and sets up the UI components and click listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_screen) // Set the layout file

        // Initialize the UI components
        nickNameInput = findViewById(R.id.nickname_input) // Nickname input field
        createBtn = findViewById(R.id.create_btn) // "Create" button for signup
        alreadyTv = findViewById(R.id.already_tv) // "Already have an account?" text view

        // Set an onClickListener for the create button to validate nickname and initiate signup
        createBtn.setOnClickListener {
            val nickname = nickNameInput.text.toString().trim() // Get and trim input text

            // Check if nickname is not empty and doesn't contain spaces
            if (nickname.isNotEmpty() && !nickname.contains(" ")) {
                sendSignupData(nickname) // Call function to send signup data to the server
            } else {
                // Display error message if nickname is empty or contains spaces
                Toast.makeText(this, "Nickname cannot be empty or contain spaces", Toast.LENGTH_SHORT).show()
            }
        }

        // Set an onClickListener for the "Already have an account?" text view to go to the login page
        alreadyTv.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java) // Start LoginPage activity
            startActivity(intent)
        }

        // Adjust the view's padding to avoid overlap with system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Function to send the signup data (nickname) to the server
    private fun sendSignupData(nickname: String) {
        // Configure Retrofit instance for network operations
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Base URL for the API endpoint
            .addConverterFactory(GsonConverterFactory.create()) // Gson converter for JSON parsing
            .build()

        // Create an instance of the ApiServiceSignUp interface
        val apiService = retrofit.create(ApiServiceSignUp::class.java)

        // Make asynchronous network request to the signup endpoint
        apiService.signup(nickname).enqueue(object : Callback<ResponseBody> {
            // onResponse: called when the server responds
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Get server response body as a string
                    val responseBody = response.body()?.string()

                    // Check if nickname is already taken
                    if (responseBody == "Nickname already taken") {
                        // Show a message if the nickname is unavailable
                        Toast.makeText(this@SignUpScreen, "Nickname already taken", Toast.LENGTH_SHORT).show()
                    } else {
                        // Signup successful message and welcome the user
                        Toast.makeText(this@SignUpScreen, "Signup Successful!", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@SignUpScreen, "Hi, $nickname!", Toast.LENGTH_SHORT).show()

                        // Save the nickname to SharedPreferences for future retrieval
                        val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putString("nickname", nickname).apply()
                        Log.d("saveNickname", "Nickname saved: $nickname") // Log saved nickname

                        // Redirect the user to MainActivity after successful signup
                        val intent = Intent(this@SignUpScreen, MainActivity::class.java)
                        intent.putExtra("signUp_nickname", nickname) // Pass nickname to MainActivity
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    // If response fails, show a message with the error details
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        this@SignUpScreen,
                        "Signup Failed. ${response.message()} - $errorBody",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // onFailure: called when network request fails due to connectivity issues or server errors
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Display error message in case of network failure
                Toast.makeText(this@SignUpScreen, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
