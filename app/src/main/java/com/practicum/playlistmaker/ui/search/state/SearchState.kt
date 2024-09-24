package com.practicum.playlistmaker.ui.search.state

import com.practicum.playlistmaker.domain.search.model.Track

sealed interface SearchState {
    data object Loading : SearchState
    data class Content(val trackList: List<Track>) : SearchState
    data object NothingFound : SearchState
    data object ConnectionProblem : SearchState
}