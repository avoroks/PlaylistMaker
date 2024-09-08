package com.practicum.playlistmaker

import android.media.MediaPlayer
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
import com.practicum.playlistmaker.data.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    private lateinit var playButton: ImageButton
    private lateinit var trackProgress: TextView

    private var mediaPlayer = MediaPlayer()

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

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }

        val backButton = this.findViewById<MaterialToolbar>(R.id.back_from_player)

        backButton.setOnClickListener {
            this.finish()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(createUpdateTrackTimeTask())
        mediaPlayer.release()
    }

    private fun playbackControl() =
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
            else -> {}
        }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_PREPARED

            handler.removeCallbacks(createUpdateTrackTimeTask())
            trackProgress.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        handler.post(createUpdateTrackTimeTask())
        playButton.setImageResource(R.drawable.ic_pause);
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        handler.removeCallbacks(createUpdateTrackTimeTask())
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play);
        playerState = STATE_PAUSED
    }

    private fun createUpdateTrackTimeTask() = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                trackProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, 300L)
            }
        }
    }
}