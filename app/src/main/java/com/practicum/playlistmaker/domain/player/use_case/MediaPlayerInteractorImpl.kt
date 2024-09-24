package com.practicum.playlistmaker.domain.player.use_case

import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.domain.player.repository.MediaPlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale


class MediaPlayerInteractorImpl(private val repository: MediaPlayerRepository) :
    MediaPlayerInteractor {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun playbackControl(
        onStartAction: () -> Unit,
        onPauseAction: () -> Unit
    ) = repository.playbackControl(
        onStartAction,
        onPauseAction
    )

    override fun preparePlayer(
        track: Track,
        onPreparedAction: () -> Unit,
        onCompletionAction: () -> Unit
    ) = repository.preparePlayer(
        track,
        onPreparedAction,
        onCompletionAction
    )

    override fun startPlayer(onStartAction: () -> Unit) = repository.startPlayer(onStartAction)
    override fun pausePlayer(onPauseAction: () -> Unit) = repository.pausePlayer(onPauseAction)
    override fun releaseResources() = repository.releaseResources()
    override fun isPlayerActive() = repository.isPlayerActive()
    override fun getCurrentPlayerPosition(): String =
        dateFormat.format(repository.getCurrentPlayerPosition())
}