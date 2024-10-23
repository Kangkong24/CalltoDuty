package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
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

interface ApiServiceUpdate {
    @FormUrlEncoded
    @POST("update.php") // For Update
    fun updateNickname(
        @Field("old_nickname") oldNickname: String,
        @Field("new_nickname") newNickname: String
    ): Call<ResponseBody>
}

class ChangeNicknamePage : AppCompatActivity() {

    private lateinit var closeButton: ImageView
    private lateinit var nicknameInput: EditText
    private lateinit var changeButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nickname_page)

        closeButton = findViewById(R.id.closeButton)
        nicknameInput = findViewById(R.id.nicknameInput)
        changeButton = findViewById(R.id.changeButton)

        // Retrieve old nickname from the Intent
        val oldNickname = intent.getStringExtra("currentNickname") ?: ""
        val signUpNN = intent.getStringExtra("signUp_nickname") ?: ""

        closeButton.setOnClickListener {
            finish() // Closes the current activity and returns to the previous one
        }

        changeButton.setOnClickListener {
            val newNickname = nicknameInput.text.toString().trim()

            if (newNickname.isNotEmpty() && !newNickname.contains(" ")) {
                updateNickname(oldNickname, newNickname)
                updateNickname(signUpNN, newNickname)
            } else {
                Toast.makeText(this, "New nickname cannot be empty or contain spaces", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateNickname(oldNickname: String, newNickname: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") // Change to your device's IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiServiceUpdate::class.java)

        apiService.updateNickname(oldNickname, newNickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()?.trim()
                    Log.d("ChangeNicknamePage", "Response Body: $responseBody")
                    if (responseBody == "Nickname updated successfully") {
                        Toast.makeText(this@ChangeNicknamePage, "Nickname updated", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ChangeNicknamePage, OptionFragment::class.java)
                        intent.putExtra("updatedNickname", newNickname)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear previous activities
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ChangeNicknamePage, "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ChangeNicknamePage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
