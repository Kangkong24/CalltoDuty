package com.example.calltoduty


import com.example.calltoduty.R

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val animationDrawable = progressBar.progressDrawable as AnimationDrawable
        animationDrawable.start()

        //Ito Yung Loading
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SignUpScreen::class.java)
            startActivity(intent)
            finish()
        }, 3000)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}