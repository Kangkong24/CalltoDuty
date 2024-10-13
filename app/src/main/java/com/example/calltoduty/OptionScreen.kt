package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OptionScreen : AppCompatActivity() {

    private lateinit var closeButton : ImageView
    private lateinit var musicSwitch : Switch
    private lateinit var soundSwitch : Switch
    private lateinit var cnButton : ImageView
    private lateinit var deleteButton : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_option_screen)

        closeButton = findViewById(R.id.closeButton)
        musicSwitch = findViewById(R.id.musicSwitch)
        soundSwitch = findViewById(R.id.soundSwitch)
        cnButton = findViewById(R.id.cnButton)
        deleteButton = findViewById(R.id.deleteButton)

        val signUpNN = intent.getStringExtra("signUp_nickname")
        val currentNickname = intent.getStringExtra("currentNickname")
        val updatedNickname = intent.getStringExtra("updatedNickname")

        closeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("currentNickname", currentNickname)
            intent.putExtra("signUp_nickname", signUpNN)
            intent.putExtra("updatedNickname", updatedNickname)
            startActivity(intent)
        }

        cnButton.setOnClickListener{
            // Get the current nickname
            val intent = Intent(this, ChangeNicknamePage::class.java)
            intent.putExtra("currentNickname", currentNickname) // Pass it to ChangeNicknamePage
            intent.putExtra("signUp_nickname", signUpNN)
            intent.putExtra("updatedNickname", updatedNickname)
            startActivity(intent)
        }


        deleteButton.setOnClickListener{
            val intent = Intent(this, DeletionPage::class.java)
            intent.putExtra("currentNickname", currentNickname)
            intent.putExtra("signUp_nickname", signUpNN)
            intent.putExtra("updatedNickname", updatedNickname)
            startActivity(intent)
        }

    }
}