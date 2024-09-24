package com.practicum.playlistmaker.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.ui.media.MediaActivity
import com.practicum.playlistmaker.ui.settings.activity.SettingsActivity
import com.practicum.playlistmaker.ui.search.activity.SearchActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSearch.setOnClickListener {
            val displaySearch = Intent(this, SearchActivity::class.java)
            startActivity(displaySearch)
        }

        val displayMedia = Intent(this, MediaActivity::class.java)

        val clickListenerForMedia = object : OnClickListener {
            override fun onClick(v: View?) {
                startActivity(displayMedia)
            }
        }

        binding.buttonMediafiles.setOnClickListener(clickListenerForMedia)

        binding.buttonSettings.setOnClickListener {
            val displaySettings = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettings)
        }
    }
}