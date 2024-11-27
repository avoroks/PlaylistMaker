package com.practicum.playlistmaker.ui.media.state

import com.practicum.playlistmaker.domain.search.model.Track

sealed interface FavoriteTracksState {
    data object EmptyState : FavoriteTracksState
    data class ContentState(val tracks: List<Track>) : FavoriteTracksState
}