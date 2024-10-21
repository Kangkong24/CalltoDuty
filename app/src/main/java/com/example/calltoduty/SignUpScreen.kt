package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
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

interface ApiServiceSignUp {
    @FormUrlEncoded
    @POST("signup.php") // Ensure this matches your server endpoint
    fun signup(
        @Field("nickname") nickname: String
    ): Call<ResponseBody>
}

class SignUpScreen : AppCompatActivity() {



    private lateinit var nickNameInput: EditText
    private lateinit var createBtn: Button
    private lateinit var alreadyTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_screen)

        nickNameInput = findViewById(R.id.nickname_input)
        createBtn = findViewById(R.id.create_btn)
        alreadyTv = findViewById(R.id.already_tv)

        createBtn.setOnClickListener {
            val nickname = nickNameInput.text.toString()

            if (nickname.isNotEmpty()) {
                sendSignupData(nickname)
            } else {
                Toast.makeText(this, "Nickname cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        //temporary
        alreadyTv.setOnClickListener{
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun sendSignupData(nickname: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.16/") //Device IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiServiceSignUp::class.java)


        apiService.signup(nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (responseBody == "Nickname already taken") {
                        Toast.makeText(this@SignUpScreen, "Nickname already taken", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SignUpScreen, "Signup Successful!", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@SignUpScreen, "Hi, $nickname!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUpScreen, MainActivity::class.java)
                        intent.putExtra("signUp_nickname", nickname) // Pass the nickname as current nickname
                        startActivity(intent)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        this@SignUpScreen,
                        "Signup Failed. ${response.message()} - $errorBody",
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