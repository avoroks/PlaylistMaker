package com.practicum.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.media.use_case.PlaylistsInteractor
import com.practicum.playlistmaker.ui.media.state.PlaylistsState
import kotlinx.coroutines.launch

open class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {
    private val playlistsState = MutableLiveData<PlaylistsState>()
    fun getPlaylistsState(): LiveData<PlaylistsState> = playlistsState

    init {
        viewModelScope.launch {
            updatePlaylists()
        }
    }

    fun updatePlaylists() = viewModelScope.launch {
        playlistsInteractor.getPlaylists().collect {
            val state =
                if (it.isEmpty()) PlaylistsState.EmptyState else PlaylistsState.ContentState(it)
            playlistsState.postValue(state)
        }
    }

    fun createPlaylist(name: String, description: String, url: String) =
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(
                Playlist(
                    name = name,
                    description = description,
                    imageUrl = url
                )
            )
        }
}