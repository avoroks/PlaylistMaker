package com.practicum.playlistmaker.ui.player.state

sealed interface PlayerState {
    data object Prepared : PlayerState
    data class Playing(val trackTime: String) : PlayerState
    data object Paused : PlayerState
    data object TrackEnded : PlayerState
}