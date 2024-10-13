package com.example.calltoduty

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

object MusicManager {
    private val soundMap: MutableMap<String, MediaPlayer> = mutableMapOf()

    fun initialize(context: Context, soundName: String, soundResId: Int, loop: Boolean, volume: Float) {
        if (soundMap[soundName] == null) {
            soundMap[soundName] = MediaPlayer.create(context, soundResId).apply {
                isLooping = loop // Set looping based on the parameter
                setVolume(volume, volume) // Set the initial volume
            }
        }
    }

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


    fun stopSound(soundName: String) {
        soundMap[soundName]?.pause()
    }

    fun release() {
        soundMap.forEach { (_, player) ->
            player.release()
        }
        soundMap.clear()
    }

    fun isPlaying(soundName: String): Boolean {
        return soundMap[soundName]?.isPlaying ?: false
    }

    fun setSoundVolume(soundName: String, volume: Float) {
        soundMap[soundName]?.setVolume(volume, volume)  // Adjust volume for the specified sound
    }




    /*private var mediaPlayer: MediaPlayer? = null

    fun initialize(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.bg_music).apply {
                isLooping = true
            }
        }
    }

    fun startMusic() {
        mediaPlayer?.start()
    }

    fun stopMusic() {
        mediaPlayer?.pause()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlaying() = mediaPlayer?.isPlaying ?: false*/

}
