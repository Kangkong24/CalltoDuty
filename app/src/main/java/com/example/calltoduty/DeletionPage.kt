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

interface ApiServiceDelete {
    @FormUrlEncoded
    @POST("delete.php") // For Delete
    fun deleteAccount(@Field("nickname") nickname: String): Call<ResponseBody>
}

class DeletionPage : AppCompatActivity() {

    private lateinit var yesButton: Button
    private lateinit var noButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deletion_page)

        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)

        val oldNickname = intent.getStringExtra("currentNickname") ?: ""
        val signUpNN = intent.getStringExtra("signUp_nickname") ?: ""
        val updatedNickname = intent.getStringExtra("updatedNickname") ?: ""

        noButton.setOnClickListener {
            finish() // Closes the current activity and returns to the previous one
        }

        yesButton.setOnClickListener {
            // Use the oldNickname for deletion
            when {
                oldNickname.isNotEmpty() -> deleteAccount(oldNickname)
                signUpNN.isNotEmpty() -> deleteAccount(signUpNN)
                updatedNickname.isNotEmpty() -> deleteAccount(updatedNickname)
                else -> Toast.makeText(this, "Nickname is not set.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteAccount(nickname: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Change to your device's IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiServiceDelete::class.java)

        apiService.deleteAccount(nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DeletionPage, "Account deleted", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@DeletionPage, SignUpScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear previous activities
                    startActivity(intent)
                } else {
                    Toast.makeText(this@DeletionPage, "Deletion failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@DeletionPage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}