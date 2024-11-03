package com.example.calltoduty

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

// Object to manage playing and controlling music
object MusicManager {
    // Map to store sound names and their corresponding MediaPlayer instances
    private val soundMap: MutableMap<String, MediaPlayer> = mutableMapOf()

    // Initialize a sound by creating a MediaPlayer instance
    fun initialize(context: Context, soundName: String, soundResId: Int, loop: Boolean, volume: Float) {
        if (soundMap[soundName] == null) {
            // Create MediaPlayer for the sound resource and configure looping and volume
            soundMap[soundName] = MediaPlayer.create(context, soundResId).apply {
                isLooping = loop // Set looping based on the parameter
                setVolume(volume, volume) // Set the initial volume
            }
        }
    }

    // Start playing the sound if it is not already playing
    fun startSound(soundName: String) {
        val player = soundMap[soundName]
        if (player != null) {
            if (!player.isPlaying) {
                player.start()  // Start the sound if it's not playing
            }
        } else {
            Log.e("MusicManager", "Sound $soundName not found")
        }
    }

    // Pause the sound if it is playing
    fun stopSound(soundName: String) {
        soundMap[soundName]?.pause()
    }

    // Release all MediaPlayer resources and clear the map
    fun release() {
        soundMap.forEach { (_, player) ->
            player.release() // Release each MediaPlayer instance
        }
        soundMap.clear() // Clear the map
    }

    // Check if a sound is currently playing
    fun isPlaying(soundName: String): Boolean {
        return soundMap[soundName]?.isPlaying ?: false
    }

    // Set the volume for a specific sound
    fun setSoundVolume(soundName: String, volume: Float) {
        soundMap[soundName]?.setVolume(volume, volume)  // Adjust volume for the specified sound
    }

}
