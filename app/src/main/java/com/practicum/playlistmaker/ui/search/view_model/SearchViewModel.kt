package com.practicum.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.domain.search.use_case.TracksInteractor
import com.practicum.playlistmaker.ui.search.state.HistoryState
import com.practicum.playlistmaker.ui.search.state.SearchState
import kotlinx.coroutines.launch

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val searchState = MutableLiveData<SearchState>()
    fun getSearchState(): LiveData<SearchState> = searchState

    private val historyState = MutableLiveData<HistoryState>()
    fun getHistoryState(): LiveData<HistoryState> = historyState


    init {
        val history = tracksInteractor.getTrackHistory()
        historyState.value = if (history.isEmpty()) HistoryState.EmptyHistory else
            HistoryState.Content(history)
    }

    fun findTracks(text: String) {
        searchState.postValue(SearchState.Loading)

        viewModelScope.launch {
            tracksInteractor
                .searchTracks(text)
                .collect { pair ->
                    val trackList = pair.first
                    val error = pair.second
                    when {
                        (trackList?.isEmpty() == true) -> searchState.postValue(SearchState.NothingFound)
                        (error != null) -> searchState.postValue(SearchState.ConnectionProblem)
                        else -> searchState.postValue(SearchState.Content(pair.first!!))
                    }
                }
        }
    }

    fun saveTrackToHistory(track: Track) {
        tracksInteractor.saveTrackToHistory(track)
        val tracks = tracksInteractor.getTrackHistory()
        historyState.postValue(HistoryState.Content(tracks))
    }

    fun clearHistory() {
        tracksInteractor.clearTrackHistory()
        historyState.postValue(HistoryState.EmptyHistory)
    }
}