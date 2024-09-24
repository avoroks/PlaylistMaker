package com.practicum.playlistmaker.ui.player.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.internal.ViewUtils
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.player.state.PlayerState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel


class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var viewModel: PlayerViewModel

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        IntentCompat.getSerializableExtra(intent, "TRACK", Track::class.java)?.let { track = it }

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.factory(track)
        )[PlayerViewModel::class.java]

        binding.track.text = track.trackName
        binding.author.text = track.artistName
        binding.trackTimeMills.text = track.humanReadableTrackTime
        binding.collectionName.text = track.collectionName
        binding.releaseDate.text = track.releaseYear
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country

        Glide.with(this)
            .load(track.coverUrl)
            .placeholder(R.drawable.track_placeholder)
            .error(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(ViewUtils.dpToPx(this, 8).toInt()))
            .into(findViewById(R.id.cover))

        binding.playButton.setOnClickListener {
            viewModel.playBackControl()
        }

        viewModel.getPlayerState().observe(this) { playerState ->
            when (playerState) {
                is PlayerState.Prepared -> {
                    binding.playButton.setImageResource(R.drawable.ic_play)
                    binding.trackTime.text = getString(R.string.default_zero_time)
                }

                PlayerState.Paused -> {
                    binding.playButton.setImageResource(R.drawable.ic_play)
                }

                is PlayerState.Playing -> {
                    binding.playButton.setImageResource(R.drawable.ic_pause)
                    binding.trackTime.text = playerState.trackTime
                }

                PlayerState.TrackEnded -> {
                    binding.trackTime.text = getString(R.string.default_zero_time)
                    binding.playButton.setImageResource(R.drawable.ic_play)
                }
            }
        }

        val backButton = this.findViewById<MaterialToolbar>(R.id.back_from_player)

        backButton.setOnClickListener {
            this.finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopPlayer()
    }
}