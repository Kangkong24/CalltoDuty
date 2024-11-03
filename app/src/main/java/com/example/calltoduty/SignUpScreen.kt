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

// Interface for the Retrofit API service to handle sign up requests
interface ApiServiceSignUp {
    @FormUrlEncoded
    @POST("signup.php") // Ensure this matches your server endpoint
    fun signup(
        @Field("nickname") nickname: String
    ): Call<ResponseBody> // Call object for handling the server response
}

// Activity class for the sign-up screen
class SignUpScreen : AppCompatActivity() {

    // Declare UI elements: an EditText for nickname input, a Button for create action, and a TextView for existing user action
    private lateinit var nickNameInput: EditText
    private lateinit var createBtn: Button
    private lateinit var alreadyTv: TextView

    // Function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_screen) // Set the layout for this activity

        nickNameInput = findViewById(R.id.nickname_input) // Initialize the nickname input field
        createBtn = findViewById(R.id.create_btn) // Initialize the create button
        alreadyTv = findViewById(R.id.already_tv) // Initialize the already a user text view

        // Set up the create button to handle the sign-up request
        createBtn.setOnClickListener {
            val nickname = nickNameInput.text.toString().trim()

            // Check if nickname is not empty and doesn't contain spaces
            if (nickname.isNotEmpty() && !nickname.contains(" ")) {
                sendSignupData(nickname)
            } else {
                Toast.makeText(this, "Nickname cannot be empty or contain spaces", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the already a user text view to navigate to the login page
        alreadyTv.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Function to send sign-up data to the server
    private fun sendSignupData(nickname: String) {
        // Set up Retrofit for making the HTTP request
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Device IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiServiceSignUp::class.java)
        // Make the network call to send the sign-up data
        apiService.signup(nickname).enqueue(object : Callback<ResponseBody> {
            // Called when the server responds
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) { // Check if the response is successful
                    val responseBody = response.body()?.string()
                    if (responseBody == "Nickname already taken") {
                        Toast.makeText(this@SignUpScreen, "Nickname already taken", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SignUpScreen, "Signup Successful!", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@SignUpScreen, "Hi, $nickname!", Toast.LENGTH_SHORT).show()

                        // Save the nickname to SharedPreferences
                        val sharedPreferences = getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putString("nickname", nickname).apply()
                        Log.d("saveNickname", "Nickname saved: $nickname")

                        // Navigate to the main activity
                        val intent = Intent(this@SignUpScreen, MainActivity::class.java)
                        intent.putExtra("signUp_nickname", nickname)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    // Show an error message if the response is not successful
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        this@SignUpScreen,
                        "Signup Failed. ${response.message()} - $errorBody",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Called if the network request fails
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@SignUpScreen, "Error: ${t.message}", Toast.LENGTH_SHORT).show() // Show an error message with the throwable message
            }
        })
    }

}
