package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {
    @FormUrlEncoded
    @POST("signup")
    fun signup(
        @Field("nickname") nickname: String
    ): Call<ResponseBody>
}

class SignUpScreen : AppCompatActivity() {

    private lateinit var nickNameInput: EditText
    private lateinit var createBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_screen)

        nickNameInput = findViewById(R.id.nickname_input)
        createBtn = findViewById(R.id.create_btn)

        createBtn.setOnClickListener {
            val nickNameInput = nickNameInput.text.toString()

            if (nickNameInput.isNotEmpty()) {
                sendSignupData(nickNameInput)
            } else {
                Toast.makeText(this, "Nickname cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
        private fun sendSignupData(nickname: String) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()




            val apiService = retrofit.create(ApiService::class.java)

            apiService.signup(nickname).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@SignUpScreen, "Signup Successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUpScreen, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@SignUpScreen,
                            "Signup Failed. ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@SignUpScreen, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
}
