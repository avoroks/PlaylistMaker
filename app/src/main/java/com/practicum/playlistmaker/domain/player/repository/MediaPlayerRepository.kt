package com.practicum.playlistmaker.domain.player.repository

import com.practicum.playlistmaker.domain.search.model.Track

interface MediaPlayerRepository {
    fun playbackControl(
        onStartAction: () -> Unit = {},
        onPauseAction: () -> Unit = {}
    )

    fun preparePlayer(
        track: Track,
        onPreparedAction: () -> Unit = {},
        onCompletionAction: () -> Unit
    )

    fun startPlayer(onStartAction: () -> Unit = {})
    fun pausePlayer(onPauseAction: () -> Unit = {})
    fun isPlayerActive(): Boolean
    fun getCurrentPlayerPosition(): Int
    fun releaseResources()
}