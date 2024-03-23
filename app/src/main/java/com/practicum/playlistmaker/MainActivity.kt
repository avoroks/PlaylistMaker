package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import com.practicum.playlistmaker.R.id.button_mediafiles
import com.practicum.playlistmaker.R.id.button_search
import com.practicum.playlistmaker.R.id.button_settings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(button_search)

        searchButton.setOnClickListener {
            val displaySearch = Intent(this, SearchActivity::class.java)
            startActivity(displaySearch)
        }

        val mediaButton = findViewById<Button>(button_mediafiles)
        val displayMedia = Intent(this, MediaActivity::class.java)

        val clickListenerForMedia = object : OnClickListener {
            override fun onClick(v: View?) {
                startActivity(displayMedia)
            }
        }

        mediaButton.setOnClickListener(clickListenerForMedia)

        val settingsButton = findViewById<Button>(button_settings)

        settingsButton.setOnClickListener {
            val displaySettings = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettings)
        }
    }
}