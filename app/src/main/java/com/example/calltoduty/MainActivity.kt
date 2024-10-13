package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sign


class MainActivity : AppCompatActivity() {

    private lateinit var playButton : ImageView
    private lateinit var optionBtn : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        playButton = this.findViewById(R.id.playButton)
        optionBtn = this.findViewById(R.id.optionBtn)

        val currentNickname = intent.getStringExtra("currentNickname") // Get the current nickname
        val signUpNN = intent.getStringExtra("signUp_nickname")
        val updatedNickname = intent.getStringExtra("updatedNickname")

        playButton.setOnClickListener{
            val intent = Intent(this, GameDifficulty::class.java)
            startActivity(intent)
        }

        optionBtn.setOnClickListener{
            // Get the current nickname
            val intent = Intent(this, OptionScreen::class.java)
            intent.putExtra("currentNickname", currentNickname) // Pass it to OptionScreen
            intent.putExtra("signUp_nickname", signUpNN)
            startActivity(intent)


        }

        /*
        Log.i("tag","Hello")
        Log.i("tag","World") */

    }
}