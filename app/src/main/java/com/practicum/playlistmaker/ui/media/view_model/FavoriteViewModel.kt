package com.practicum.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.media.use_case.FavoriteTracksInteractor
import com.practicum.playlistmaker.ui.media.state.FavoriteTracksState
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteTracksInteractor: FavoriteTracksInteractor) :
    ViewModel() {
    private val favoriteTracksState = MutableLiveData<FavoriteTracksState>()
    fun getFavoriteTracksState(): LiveData<FavoriteTracksState> = favoriteTracksState

    init {
        viewModelScope.launch {
            updateFavoriteTracks()
        }
    }

    fun updateFavoriteTracks() = viewModelScope.launch {
        favoriteTracksInteractor.getFavoriteTracks().collect {
            val state =
                if (it.isEmpty()) FavoriteTracksState.EmptyState else FavoriteTracksState.ContentState(it)
            favoriteTracksState.postValue(state)
        }
    }
}