package com.practicum.playlistmaker.ui.search.state

import com.practicum.playlistmaker.domain.search.model.Track

sealed interface HistoryState {
    data class Content(val trackList: List<Track>) : HistoryState
    data object EmptyHistory : HistoryState
}