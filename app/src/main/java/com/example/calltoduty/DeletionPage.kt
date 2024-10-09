package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DeletionPage : AppCompatActivity() {
    private lateinit var yesButton : ImageView
    private lateinit var noButton : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deletion_page)

        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)

        noButton.setOnClickListener{
            val intent = Intent(this, OptionPage::class.java)
            startActivity(intent)
        }

    }
}