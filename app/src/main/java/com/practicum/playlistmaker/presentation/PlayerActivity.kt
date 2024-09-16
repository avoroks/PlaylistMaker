package com.practicum.playlistmaker.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.internal.ViewUtils
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.model.Track


class PlayerActivity : AppCompatActivity() {

    private val getPlayerInteractor = Creator.provideMediaPlayerInteractor()

    private lateinit var playButton: ImageButton
    private lateinit var trackProgress: TextView

    private lateinit var track: Track

    private val handler = Handler(Looper.getMainLooper())

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

        playButton = findViewById(R.id.playButton)
        trackProgress = findViewById(R.id.trackTime)

        getPlayerInteractor.preparePlayer(
            track = track,
            onCompletionAction = {
                playButton.setImageResource(R.drawable.ic_play)
                handler.removeCallbacks(createUpdateTrackTimeTask())
                trackProgress.text = getString(R.string.default_zero_time)
            }
        )

        playButton.setOnClickListener {
            getPlayerInteractor.playbackControl(
                onStartAction = {
                    handler.post(createUpdateTrackTimeTask())
                    playButton.setImageResource(R.drawable.ic_pause)
                },
                onPauseAction = {
                    handler.removeCallbacks(createUpdateTrackTimeTask())
                    playButton.setImageResource(R.drawable.ic_play)
                }
            )
        }

        val backButton = this.findViewById<MaterialToolbar>(R.id.back_from_player)

        backButton.setOnClickListener {
            this.finish()
        }
    }

    override fun onPause() {
        super.onPause()
        getPlayerInteractor.pausePlayer()
        playButton.setImageResource(R.drawable.ic_play)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(createUpdateTrackTimeTask())
        getPlayerInteractor.releaseResources()
    }

    private fun createUpdateTrackTimeTask() = object : Runnable {
        override fun run() {
            if (getPlayerInteractor.isPlayerActive()) {
                trackProgress.text = getPlayerInteractor.getCurrentPlayerPosition()
                handler.postDelayed(this, DELAY_FOR_UPDATE_TRACK_TIME)
            }
        }
    }

    companion object {
        private const val DELAY_FOR_UPDATE_TRACK_TIME = 300L
    }
}