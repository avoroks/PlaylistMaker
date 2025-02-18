package com.practicum.playlistmaker.ui.player.state

sealed interface AddTrackToPlaylistState {
    data class TrackAdded(val playlistName: String) : AddTrackToPlaylistState
    data class TrackAlreadyExistsInPlaylist(val playlistName: String) : AddTrackToPlaylistState
}