package com.example.calltoduty

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

interface ApiServiceLogin {
    @FormUrlEncoded
    @POST("login.php") // For Read
    fun login(@Field("nickname") nickname: String): Call<ResponseBody>
}

class LoginPage : AppCompatActivity() {

    private lateinit var nickNameInput: EditText
    private lateinit var enterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        nickNameInput = findViewById(R.id.nn_input)
        enterButton = findViewById(R.id.enterButton)

        enterButton.setOnClickListener {
            val nickname = nickNameInput.text.toString().trim() // Trim whitespace

            // Check if nickname is not empty and doesn't contain spaces
            if (nickname.isNotEmpty() && !nickname.contains(" ")) {
                checkLogin(nickname)
            } else {
                Toast.makeText(this, "Nickname cannot be empty or contain spaces", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLogin(nickname: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.61/rest_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiServiceLogin::class.java)

        Log.d("LoginAttempt", "Attempting to login with nickname: $nickname")

        apiService.login(nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Response Code", "Response Code: ${response.code()}") // Log response code

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d("LoginResponse", responseBody ?: "null")

                    // Check the response from the server (plain string)
                    if (responseBody != null) {
                        val trimmedResponse = responseBody.trim()
                        Log.d("Trimmed Response", trimmedResponse)

                        when (trimmedResponse) {
                            "Login successful" -> {
                                Toast.makeText(this@LoginPage, "Hi, $nickname!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginPage, MainActivity::class.java)
                                intent.putExtra("currentNickname", nickname)
                                startActivity(intent)
                            }
                            "User not found" -> {
                                Toast.makeText(this@LoginPage, "Nickname not found", Toast.LENGTH_SHORT).show()
                            }
                            "No progress found for user" -> {
                                Toast.makeText(this@LoginPage, "No progress found", Toast.LENGTH_SHORT).show()
                            }
                            "Nickname cannot be empty or contain spaces" -> {
                                Toast.makeText(this@LoginPage, "Invalid nickname", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this@LoginPage, "Unknown response from server", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginPage, "Empty response from server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginPage, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@LoginPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
