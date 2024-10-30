package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Declare UI components for play button, options button, and credits button
    private lateinit var playButton: ImageView
    private lateinit var optionBtn: ImageView
    private lateinit var creditsBtn: ImageView

    // onCreate is the entry point for the activity lifecycle where initialization occurs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge display for a modern look
        setContentView(R.layout.activity_main) // Set the layout for this activity

        // Initialize the UI components by finding them by their IDs in the layout
        playButton = this.findViewById(R.id.playButton) // Reference to the play button
        optionBtn = this.findViewById(R.id.optionBtn) // Reference to the options button
        creditsBtn = this.findViewById(R.id.creditsButton) // Reference to the credits button

        // Initialize the music manager to handle background music settings
        MusicManager.initialize(this, "bg_music", R.raw.bg_music, loop = true, volume = -5.0f)
        MusicManager.startSound("bg_music") // Start playing the background music

        // Retrieve current nickname values passed from other activities via intents
        val signUpNN = intent.getStringExtra("signUp_nickname") // Get the sign-up nickname
        val updatedNickname = intent.getStringExtra("updatedNickname") // Get the updated nickname, if available
        val currentNickname = intent.getStringExtra("currentNickname") // Get the current nickname, if available

        // Set an onClick listener for the play button
        playButton.setOnClickListener {
            // Create an Intent to start the GameDifficulty activity
            val intent = Intent(this, GameDifficulty::class.java)
            startActivity(intent) // Start the GameDifficulty activity
        }

        // Set an onClick listener for the options button
        optionBtn.setOnClickListener {
            // Create an instance of the OptionFragment and set arguments
            val optionFragment = OptionFragment.newInstance().apply {
                arguments = Bundle().apply {
                    // Pass the current nickname, sign-up nickname, and updated nickname to the fragment
                    putString("currentNickname", currentNickname)
                    putString("signUp_nickname", signUpNN)
                    putString("updatedNickname", updatedNickname)
                }
            }
            // Show the OptionFragment using the FragmentManager
            optionFragment.show(supportFragmentManager, "OptionFragment")
        }

        // Set an onClick listener for the credits button
        creditsBtn.setOnClickListener {
            // Create and show the CreditsFragment instance
            CreditsFragment.newInstance().show(supportFragmentManager, "creditsFragment")
        }

        /*
        Log statements for debugging can be added here to check the flow of the application.
        Example: Log.i("tag","Hello")
        Example: Log.i("tag","World")
        */
    }

    // onDestroy is called when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy() // Call the superclass method to perform cleanup
        MusicManager.release() // Release the music resources to avoid memory leaks
    }
}
