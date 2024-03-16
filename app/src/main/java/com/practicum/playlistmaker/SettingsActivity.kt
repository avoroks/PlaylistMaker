package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = this.findViewById<ImageView>(R.id.back_from_settings)

        backButton.setOnClickListener {
            val displayMain = Intent(this, MainActivity::class.java)
            startActivity(displayMain)
        }
    }
}