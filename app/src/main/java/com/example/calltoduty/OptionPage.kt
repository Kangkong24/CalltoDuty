package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OptionPage : AppCompatActivity() {

    private lateinit var closeButton: ImageView
    private lateinit var musicButton: ImageView
    private lateinit var musicSwitch: Switch
    private lateinit var soundButton: ImageView
    private lateinit var soundSwitch: Switch
    private lateinit var cnButton: ImageView
    private lateinit var deleteButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        closeButton = findViewById(R.id.closeButton)
        musicButton = findViewById(R.id.musicButton)
        musicSwitch = findViewById(R.id.musicSwitch)
        soundButton = findViewById(R.id.soundButton)
        soundSwitch = findViewById(R.id.soundSwitch)
        cnButton = findViewById(R.id.cnButton)
        deleteButton = findViewById(R.id.deleteButton)

        closeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }




    }
}