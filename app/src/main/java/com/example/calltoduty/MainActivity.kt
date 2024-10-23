package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
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


        MusicManager.initialize(this, "bg_music", R.raw.bg_music, loop = true, volume = -5.0f)
        MusicManager.startSound("bg_music")

        // Get the current nickname
        val signUpNN = intent.getStringExtra("signUp_nickname")
        val updatedNickname = intent.getStringExtra("updatedNickname")
        val currentNickname = intent.getStringExtra("currentNickname")

        playButton.setOnClickListener{
            val intent = Intent(this, GameDifficulty::class.java)
            startActivity(intent)
        }


        optionBtn.setOnClickListener {
            val optionFragment = OptionFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putString("currentNickname", currentNickname)
                    putString("signUp_nickname", signUpNN)
                    putString("updatedNickname", updatedNickname)
                }
            }
            optionFragment.show(supportFragmentManager, "OptionFragment")
        }







        /*optionBtn.setOnClickListener{
            // Get the current nickname
            val intent = Intent(this, OptionScreen::class.java)
            intent.putExtra("currentNickname", currentNickname) // Pass it to OptionScreen
            intent.putExtra("signUp_nickname", signUpNN)
            startActivity(intent)


        }*/

        /*
        Log.i("tag","Hello")
        Log.i("tag","World") */

    }
    override fun onDestroy() {
        super.onDestroy()
        MusicManager.release()
    }
}