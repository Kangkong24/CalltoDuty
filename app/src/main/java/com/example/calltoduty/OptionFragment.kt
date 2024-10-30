package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import androidx.fragment.app.DialogFragment

class OptionFragment : DialogFragment() {

    // Declare UI components for closing the dialog, music toggle, sound toggle, change nickname button, and delete button
    private lateinit var closeButton: ImageView
    private lateinit var musicSwitch: Switch
    private lateinit var soundSwitch: Switch
    private lateinit var cnButton: ImageView
    private lateinit var deleteButton: ImageView

    // onCreateView is called to inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_option, container, false)
    }

    // onViewCreated is called immediately after onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the UI components by finding them by their IDs in the layout
        closeButton = view.findViewById(R.id.closeButton) // Reference to the close button
        musicSwitch = view.findViewById(R.id.musicSwitch) // Reference to the music switch
        soundSwitch = view.findViewById(R.id.soundSwitch) // Reference to the sound switch
        cnButton = view.findViewById(R.id.cnButton) // Reference to the change nickname button
        deleteButton = view.findViewById(R.id.deleteButton) // Reference to the delete button

        // Restore the state of the switches based on saved instance state or current music state
        musicSwitch.isChecked = savedInstanceState?.getBoolean("musicSwitchState") ?: MusicManager.isPlaying("bg_music")
        soundSwitch.isChecked = savedInstanceState?.getBoolean("soundSwitchState") ?: MusicManager.isPlaying("gameplay_sound")

        // Retrieve the nickname values passed from the main activity
        val signUpNN = arguments?.getString("signUp_nickname") // Get sign-up nickname
        val currentNickname = arguments?.getString("currentNickname") // Get current nickname
        val updatedNickname = arguments?.getString("updatedNickname") // Get updated nickname

        // Set up a click listener for the close button to dismiss the dialog
        closeButton.setOnClickListener {
            dismiss() // Dismiss the popup dialog
        }

        // Navigate to ChangeNicknamePage when the change nickname button is clicked
        cnButton.setOnClickListener {
            val intent = Intent(activity, ChangeNicknamePage::class.java)
            // Pass the current, sign-up, and updated nicknames to the ChangeNicknamePage
            intent.putExtra("currentNickname", currentNickname)
            intent.putExtra("signUp_nickname", signUpNN)
            intent.putExtra("updatedNickname", updatedNickname)
            startActivity(intent) // Start the ChangeNicknamePage activity
        }

        // Navigate to DeletionPage when the delete button is clicked
        deleteButton.setOnClickListener {
            val intent = Intent(activity, DeletionPage::class.java)
            // Pass the current, sign-up, and updated nicknames to the DeletionPage
            intent.putExtra("currentNickname", currentNickname)
            intent.putExtra("signUp_nickname", signUpNN)
            intent.putExtra("updatedNickname", updatedNickname)
            startActivity(intent) // Start the DeletionPage activity
        }

        // Handle the music switch state changes
        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Start or stop background music based on the switch state
            if (isChecked) MusicManager.startSound("bg_music") else MusicManager.stopSound("bg_music")
        }

        // Handle the sound switch state changes
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Start or stop gameplay sound based on the switch state
            if (isChecked) MusicManager.startSound("gameplay_sound") else MusicManager.stopSound("gameplay_sound")
        }
    }

    // onSaveInstanceState is called to save the fragment's state during configuration changes
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the state of the music and sound switches for restoration later
        outState.putBoolean("musicSwitchState", musicSwitch.isChecked)
        outState.putBoolean("soundSwitchState", soundSwitch.isChecked)
    }

    // Companion object for creating a new instance of the fragment
    companion object {
        fun newInstance(): OptionFragment {
            return OptionFragment() // Return a new instance of OptionFragment
        }
    }
}
