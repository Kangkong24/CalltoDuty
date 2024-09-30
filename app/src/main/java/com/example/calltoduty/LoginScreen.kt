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



class LoginScreen :  AppCompatActivity() {

    private lateinit var nickNameInput: EditText
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        nickNameInput = findViewById(R.id.nickname_input)
        loginBtn = findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {
            val nickname = nickNameInput.text.toString()

            if (nickname.isNotEmpty()) {
                sendLoginData(nickname)
            } else {
                Toast.makeText(this, "Nickname cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendLoginData(nickname: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.login(nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d("LoginResponse", "Response: $responseBody")
                    when (responseBody) {
                        "Login successful" -> {
                            Toast.makeText(this@LoginScreen, "Login Successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginScreen, MainActivity::class.java)
                            startActivity(intent)
                        }
                        "Nickname not found" -> {
                            Toast.makeText(this@LoginScreen, "Nickname not found", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@LoginScreen, "Unexpected response: $responseBody", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("LoginResponse", "Error: $errorBody")
                    Toast.makeText(
                        this@LoginScreen,
                        "Login Failed. ${response.message()} - $errorBody",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("LoginResponse", "Failure: ${t.message}")
                Toast.makeText(this@LoginScreen, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
