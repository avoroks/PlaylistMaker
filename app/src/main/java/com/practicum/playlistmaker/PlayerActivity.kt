package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.internal.ViewUtils
import com.practicum.playlistmaker.data.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        IntentCompat.getSerializableExtra(intent, "TRACK", Track::class.java)?.let { track = it }

        val trackName = findViewById<TextView>(R.id.track)
        val artistName = findViewById<TextView>(R.id.author)
        val trackTime = findViewById<TextView>(R.id.trackTimeMills)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        val country = findViewById<TextView>(R.id.country)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.humanReadableTrackTime
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseYear
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(track.coverUrl)
            .placeholder(R.drawable.track_placeholder)
            .error(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(ViewUtils.dpToPx(this, 8).toInt()))
            .into(findViewById(R.id.cover))

        val backButton = this.findViewById<MaterialToolbar>(R.id.back_from_player)

        backButton.setOnClickListener {
            this.finish()
        }
    }
}