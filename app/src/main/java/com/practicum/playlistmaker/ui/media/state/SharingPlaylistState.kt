package com.practicum.playlistmaker.ui.media.state

sealed interface SharingPlaylistsState {
    data object EmptyTracksState : SharingPlaylistsState
    data class ContentState(val playlistData: String) : SharingPlaylistsState
}