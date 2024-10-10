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
    private lateinit var cnButton : Button
    private lateinit var deleteButton : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_option_screen)

        closeButton = findViewById(R.id.closeButton)
        musicSwitch = findViewById(R.id.musicSwitch)
        soundSwitch = findViewById(R.id.soundSwitch)
        cnButton = findViewById(R.id.cnButton)
        deleteButton = findViewById(R.id.deleteButton)

        closeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        cnButton.setOnClickListener{
            val intent = Intent(this, ChangeNIcknamePage::class.java)
            startActivity(intent)
        }

        deleteButton.setOnClickListener{
            val intent = Intent(this, DeletionPage::class.java)
            startActivity(intent)
        }

    }
}