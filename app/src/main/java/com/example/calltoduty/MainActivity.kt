package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var playButton : ImageView
    private lateinit var optionBtn : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        playButton = this.findViewById(R.id.playButton)
        optionBtn = this.findViewById(R.id.optionBtn)

        playButton.setOnClickListener{
            val intent = Intent(this, GameDifficulty::class.java)
            startActivity(intent)
        }

        optionBtn.setOnClickListener{
            val intent = Intent(this, OptionScreen::class.java)
            startActivity(intent)
        }

        /*
        Log.i("tag","Hello")
        Log.i("tag","World") */

    }
}