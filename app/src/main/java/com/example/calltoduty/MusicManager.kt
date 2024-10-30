package com.example.calltoduty

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

// Singleton object for managing music and sound effects
object MusicManager {
    // Map to hold MediaPlayer instances by their sound name
    private val soundMap: MutableMap<String, MediaPlayer> = mutableMapOf()

    // Initialize a sound in the MusicManager
    fun initialize(context: Context, soundName: String, soundResId: Int, loop: Boolean, volume: Float) {
        // Check if the sound is already initialized
        if (soundMap[soundName] == null) {
            soundMap[soundName] = MediaPlayer.create(context, soundResId).apply {
                isLooping = loop // Set looping based on the parameter
                setVolume(volume, volume) // Set the initial volume
            }
        }
    }

    // Start playing a sound by its name
    fun startSound(soundName: String) {
        val player = soundMap[soundName] // Get the MediaPlayer for the sound
        if (player != null) {
            if (!player.isPlaying) {
                player.start() // Start the sound if it's not currently playing
            }
        } else {
            Log.e("MusicManager", "Sound $soundName not found") // Log an error if the sound is not found
        }
    }

    // Stop playing a sound by its name
    fun stopSound(soundName: String) {
        soundMap[soundName]?.pause() // Pause the sound if it's found
    }

    // Release all MediaPlayer resources
    fun release() {
        soundMap.forEach { (_, player) ->
            player.release() // Release each MediaPlayer instance
        }
        soundMap.clear() // Clear the sound map
    }

    // Check if a sound is currently playing
    fun isPlaying(soundName: String): Boolean {
        return soundMap[soundName]?.isPlaying ?: false // Return true if the sound is playing, false otherwise
    }

    // Set the volume for a specific sound
    fun setSoundVolume(soundName: String, volume: Float) {
        soundMap[soundName]?.setVolume(volume, volume) // Adjust the volume for the specified sound
    }
}
