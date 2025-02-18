package com.practicum.playlistmaker.ui.media.state

import com.practicum.playlistmaker.domain.media.model.Playlist

sealed interface PlaylistsState {
    data object EmptyState : PlaylistsState
    data class ContentState(val playlists: List<Playlist>) : PlaylistsState
}