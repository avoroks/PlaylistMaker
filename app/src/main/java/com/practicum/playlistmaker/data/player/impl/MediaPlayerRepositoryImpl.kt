package com.practicum.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.domain.player.repository.MediaPlayerRepository

class MediaPlayerRepositoryImpl : MediaPlayerRepository {
    private val mediaPlayer = MediaPlayer()

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    override fun playbackControl(
        onStartAction: () -> Unit,
        onPauseAction: () -> Unit,
    ) = when (playerState) {
        STATE_PLAYING -> pausePlayer(onPauseAction)
        STATE_PREPARED, STATE_PAUSED -> startPlayer(onStartAction)
        else -> {}
    }


    override fun startPlayer(onStartAction: () -> Unit) {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        onStartAction()
    }

    override fun pausePlayer(onPauseAction: () -> Unit) {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        onPauseAction()
    }

    override fun isPlayerActive() = (playerState == STATE_PLAYING)

    override fun getCurrentPlayerPosition() = mediaPlayer.currentPosition
    override fun releaseResources() = mediaPlayer.release()

    override fun preparePlayer(
        track: Track,
        onPreparedAction: () -> Unit,
        onCompletionAction: () -> Unit
    ) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            onPreparedAction()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            onCompletionAction()
        }
    }
}