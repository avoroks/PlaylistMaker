package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val backButton = this.findViewById<TextView>(R.id.back_from_media)

        backButton.setOnClickListener {
            this.finish()
        }
    }
}