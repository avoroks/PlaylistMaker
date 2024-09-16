package com.practicum.playlistmaker.domain.use_case

import com.practicum.playlistmaker.domain.model.Track

interface MediaPlayerInteractor {
    fun playbackControl(
        onStartAction: () -> Unit = {},
        onPauseAction: () -> Unit = {}
    )

    fun preparePlayer(
        track: Track,
        onPreparedAction: () -> Unit = {},
        onCompletionAction: () -> Unit = {}
    )

    fun startPlayer(onStartAction: () -> Unit = {})
    fun pausePlayer(onPauseAction: () -> Unit = {})
    fun releaseResources()
    fun isPlayerActive(): Boolean
    fun getCurrentPlayerPosition(): String?
}