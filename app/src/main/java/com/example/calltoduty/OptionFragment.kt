package com.example.calltoduty

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import androidx.fragment.app.DialogFragment

class OptionFragment : DialogFragment() {

    private lateinit var closeButton: ImageView
    private lateinit var musicSwitch: Switch
    private lateinit var soundSwitch: Switch
    private lateinit var cnButton: ImageView
    private lateinit var deleteButton: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeButton = view.findViewById(R.id.closeButton)
        musicSwitch = view.findViewById(R.id.musicSwitch)
        soundSwitch = view.findViewById(R.id.soundSwitch)
        cnButton = view.findViewById(R.id.cnButton)
        deleteButton = view.findViewById(R.id.deleteButton)


        // Restore switch states
        musicSwitch.isChecked = savedInstanceState?.getBoolean("musicSwitchState") ?: MusicManager.isPlaying("bg_music")
        soundSwitch.isChecked = savedInstanceState?.getBoolean("soundSwitchState") ?: MusicManager.isPlaying("gameplay_sound")


        val signUpNN = arguments?.getString("signUp_nickname")
        val currentNickname = arguments?.getString("currentNickname")
        val updatedNickname = arguments?.getString("updatedNickname")

        // Close the dialog and return to MainActivity
        closeButton.setOnClickListener {
            dismiss() // Dismiss the popup
        }

        // Navigate to ChangeNicknamePage
        cnButton.setOnClickListener {
            val intent = Intent(activity, ChangeNicknamePage::class.java)
            intent.putExtra("currentNickname", currentNickname)
            intent.putExtra("signUp_nickname", signUpNN)
            intent.putExtra("updatedNickname", updatedNickname)
            startActivity(intent)
        }

        // Navigate to DeletionPage
        deleteButton.setOnClickListener {
            val intent = Intent(activity, DeletionPage::class.java)
            intent.putExtra("currentNickname", currentNickname)
            intent.putExtra("signUp_nickname", signUpNN)
            intent.putExtra("updatedNickname", updatedNickname)
            startActivity(intent)
        }


        // Handle music switch
        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) MusicManager.startSound("bg_music") else MusicManager.stopSound("bg_music")
        }

        // Handle sound switch
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) MusicManager.startSound("gameplay_sound") else MusicManager.stopSound("gameplay_sound")
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the state of the switches
        outState.putBoolean("musicSwitchState", musicSwitch.isChecked)
        outState.putBoolean("soundSwitchState", soundSwitch.isChecked)
    }


    companion object {
        fun newInstance(): OptionFragment {
            return OptionFragment()
        }
    }
}
