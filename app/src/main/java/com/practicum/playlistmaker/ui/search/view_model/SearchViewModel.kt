package com.practicum.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.search.consumer.Consumer
import com.practicum.playlistmaker.domain.search.consumer.ConsumerData
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.domain.search.use_case.TracksInteractor
import com.practicum.playlistmaker.ui.search.state.HistoryState
import com.practicum.playlistmaker.ui.search.state.SearchState

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
        tracksInteractor.searchTracks(
            expression = text,
            consumer = object : Consumer<List<Track>> {
                override fun consume(data: ConsumerData<List<Track>>) {
                    when (data) {
                        is ConsumerData.Data -> {
                            if (data.value.isNotEmpty()) searchState.postValue(
                                SearchState.Content(
                                    data.value
                                )
                            )
                            else searchState.postValue(SearchState.NothingFound)
                        }

                        is ConsumerData.Error -> {
                            searchState.postValue(SearchState.ConnectionProblem)
                        }
                    }
                }
            }
        )
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

    companion object {
        fun factory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    SearchViewModel(
                        Creator.provideTracksInteractor()
                    )
                }
            }
        }
    }
}