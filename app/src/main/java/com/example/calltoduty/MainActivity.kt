package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.OnBackPressedCallback



class MainActivity : AppCompatActivity() {

    private lateinit var playButton : ImageView
    private lateinit var optionBtn : ImageView
    private lateinit var creditsBtn : ImageView
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        playButton = this.findViewById(R.id.playButton)
        optionBtn = this.findViewById(R.id.optionBtn)
        creditsBtn = this.findViewById(R.id.creditsButton)


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


        creditsBtn.setOnClickListener {
            CreditsFragment.newInstance().show(supportFragmentManager, "creditsFragment")
        }


        // Custom back button behavior
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToast.cancel()
                    finishAffinity()
                } else {
                    backToast = Toast.makeText(applicationContext, "Press again to exit", Toast.LENGTH_SHORT)
                    backToast.show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicManager.release()
    }
}