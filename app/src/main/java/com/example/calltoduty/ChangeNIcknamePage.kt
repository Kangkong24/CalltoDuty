package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChangeNIcknamePage : AppCompatActivity() {
    private lateinit var closeButton : ImageView
    private lateinit var nicknameInput : EditText
    private lateinit var changeButton : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_nickname_page)

        closeButton = findViewById(R.id.closeButton)
        nicknameInput = findViewById(R.id.nicknameInput)
        changeButton = findViewById(R.id.changeButton)

        closeButton.setOnClickListener{
            val intent = Intent(this, OptionScreen::class.java)
            startActivity(intent)
        }

        changeButton.setOnClickListener{
            val intent = Intent(this, OptionScreen::class.java)
            startActivity(intent)
        }


    }
}