package com.example.calltoduty

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import androidx.fragment.app.DialogFragment

// DialogFragment for the options menu
class OptionFragment : DialogFragment() {

    // Declare UI elements: close button, switches for music and sound, buttons for changing nickname and deleting account
    private lateinit var closeButton: ImageView
    private lateinit var musicSwitch: Switch
    private lateinit var soundSwitch: Switch
    private lateinit var cnButton: ImageView
    private lateinit var deleteButton: ImageView

    // Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_option, container, false)
    }

    // Called after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the UI components by finding them by their IDs
        closeButton = view.findViewById(R.id.closeButton)
        musicSwitch = view.findViewById(R.id.musicSwitch)
        soundSwitch = view.findViewById(R.id.soundSwitch)
        cnButton = view.findViewById(R.id.cnButton)
        deleteButton = view.findViewById(R.id.deleteButton)


        // Restore switch states from savedInstanceState or set defaults
        musicSwitch.isChecked = savedInstanceState?.getBoolean("musicSwitchState") ?: MusicManager.isPlaying("bg_music")
        soundSwitch.isChecked = savedInstanceState?.getBoolean("soundSwitchState") ?: MusicManager.isPlaying("gameplay_sound")

        // Retrieve nickname information from arguments
        val signUpNN = arguments?.getString("signUp_nickname")
        val currentNickname = arguments?.getString("currentNickname")
        val updatedNickname = arguments?.getString("updatedNickname")

        // Close the dialog and return to MainActivity
        closeButton.setOnClickListener {
            dismiss() // Dismiss the popup
        }

        // Handle change nickname button click
        cnButton.setOnClickListener {
            // Show WarningFragment
            val changingWarningFragment = ChangingWarningFragment.newInstance("Losing progress", "Think about it!")
            changingWarningFragment.show(parentFragmentManager, "changingWarningFragment")

            // Start or Navigate to ChangeNicknamePage with a delay
            cnButton.postDelayed({
                val intent = Intent(activity, ChangeNicknamePage::class.java)
                intent.putExtra("currentNickname", currentNickname)
                intent.putExtra("signUp_nickname", signUpNN)
                intent.putExtra("updatedNickname", updatedNickname)
                startActivity(intent)
            }, 4000) // Delayed for 4 seconds.
        }

        // Handle delete account button click to navigate to DeletionPage
        deleteButton.setOnClickListener {
            val intent = Intent(activity, DeletionPage::class.java)
            intent.putExtra("currentNickname", currentNickname)
            intent.putExtra("signUp_nickname", signUpNN)
            intent.putExtra("updatedNickname", updatedNickname)
            startActivity(intent)
        }


        // Handle music switch changes
        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) MusicManager.startSound("bg_music") else MusicManager.stopSound("bg_music")
        }

        // Handle sound switch changes
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) MusicManager.startSound("gameplay_sound") else MusicManager.stopSound("gameplay_sound")
        }

    }

    // Save the state of the switches
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("musicSwitchState", musicSwitch.isChecked)
        outState.putBoolean("soundSwitchState", soundSwitch.isChecked)
    }

    // Create a non-cancelable dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)  // Prevent the dialog from being cancelled
        dialog.setCanceledOnTouchOutside(false)  // Prevent dialog dismissal by touching outside of it
        return dialog
    }

    // Companion object to create a new instance of the fragment
    companion object {
        fun newInstance(): OptionFragment {
            return OptionFragment()
        }
    }
}
