package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.OnBackPressedCallback


// Main activity class for the app
class MainActivity : AppCompatActivity() {

    // Declare UI elements: play button, options button, and credits button
    private lateinit var playButton : ImageView
    private lateinit var optionBtn : ImageView
    private lateinit var creditsBtn : ImageView

    // Variables for handling double back press to exit
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast

    // Function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge mode for immersive UI
        setContentView(R.layout.activity_main) // Set the layout for this activity

        // Initialize the UI components by finding them by their IDs
        playButton = this.findViewById(R.id.playButton)
        optionBtn = this.findViewById(R.id.optionBtn)
        creditsBtn = this.findViewById(R.id.creditsButton)

        // Initialize and start background music
        MusicManager.initialize(this, "bg_music", R.raw.bg_music, loop = true, volume = -5.0f)
        MusicManager.startSound("bg_music")

        // Get the current nickname and other data passed from previous activities
        val signUpNN = intent.getStringExtra("signUp_nickname")
        val updatedNickname = intent.getStringExtra("updatedNickname")
        val currentNickname = intent.getStringExtra("currentNickname")

        // Set click listener for play button to start the game difficulty activity
        playButton.setOnClickListener{
            val intent = Intent(this, GameDifficulty::class.java)
            startActivity(intent)
        }

        // Set click listener for options button to show the options fragment
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

        // Set click listener for credits button to show the credits fragment
        creditsBtn.setOnClickListener {
            CreditsFragment.newInstance().show(supportFragmentManager, "creditsFragment")
        }


        // Custom back button behavior to handle double press to exit
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToast.cancel()
                    finishAffinity() // Finish all activities in the stack
                } else {
                    backToast = Toast.makeText(applicationContext, "Press again to exit", Toast.LENGTH_SHORT)
                    backToast.show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
    }

    // Function called when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        MusicManager.release()// Release music resources
    }
}