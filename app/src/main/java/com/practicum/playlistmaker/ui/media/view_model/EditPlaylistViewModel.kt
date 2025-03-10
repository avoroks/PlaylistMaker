package com.practicum.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.media.use_case.PlaylistsInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor
) : PlaylistsViewModel(playlistsInteractor) {
    private val playlistState = MutableLiveData<Playlist>()
    fun getPlaylistState(): LiveData<Playlist> = playlistState


    init {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(playlistId).collect {
                playlistState.postValue(it)
            }
        }
    }

    fun updatePlaylist(name: String?, description: String?, url: String?) =
        viewModelScope.launch {
            playlistsInteractor.updatePlaylistInfo(playlistId, name, description, url)
        }
}