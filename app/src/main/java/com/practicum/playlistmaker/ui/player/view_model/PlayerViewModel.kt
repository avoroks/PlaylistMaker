package com.practicum.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.media.use_case.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.media.use_case.PlaylistsInteractor
import com.practicum.playlistmaker.domain.player.use_case.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.state.PlaylistsState
import com.practicum.playlistmaker.ui.player.state.AddTrackToPlaylistState
import com.practicum.playlistmaker.ui.player.state.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    val track: Track,
    private val playerInteractor: MediaPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>()
    fun getPlayerState(): LiveData<PlayerState> = playerState

    private val isTrackAddedToFavoriteState = MutableLiveData<Boolean>()
    fun getIsTrackAddedToFavoriteState(): LiveData<Boolean> = isTrackAddedToFavoriteState

    private val playlistsState = MutableLiveData<PlaylistsState>()
    fun getPlaylistsState(): LiveData<PlaylistsState> = playlistsState

    private val addTrackToPlaylistState = MutableLiveData<AddTrackToPlaylistState>()
    fun getAddTrackToPlaylistState(): LiveData<AddTrackToPlaylistState> = addTrackToPlaylistState

    private var timerJob: Job? = null

    init {
        playerInteractor.preparePlayer(
            track = track,
            onCompletionAction = {
                playerState.value = PlayerState.TrackEnded
                timerJob?.cancel()

            }
        )
        playerState.value = PlayerState.Prepared
        viewModelScope.launch {
            favoriteTracksInteractor.isTrackFavorite(track).collect {
                isTrackAddedToFavoriteState.value = it
            }
        }
        updatePlaylists()
    }

    fun playBackControl() {
        playerInteractor.playbackControl(
            onStartAction = {
                createUpdateTrackTimeTask()
            },
            onPauseAction = {
                playerState.value = PlayerState.Paused
                timerJob?.cancel()
            }
        )
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerState.value = PlayerState.Paused
        timerJob?.cancel()
    }

    fun stopPlayer() {
        playerInteractor.releaseResources()
        timerJob?.cancel()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val isFavorite = isTrackAddedToFavoriteState.value ?: false

            if (isFavorite) favoriteTracksInteractor.removeTrackFromFavorite(track)
            else favoriteTracksInteractor.addTrackToFavorite(track)

            isTrackAddedToFavoriteState.value = !isFavorite
        }
    }

    fun updatePlaylists() = viewModelScope.launch {
        playlistsInteractor.getPlaylists().collect {
            val state =
                if (it.isEmpty()) PlaylistsState.EmptyState else PlaylistsState.ContentState(it)
            playlistsState.postValue(state)
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        if (track.trackId.toString() in playlist.trackIds) addTrackToPlaylistState.postValue(
            AddTrackToPlaylistState.TrackAlreadyExistsInPlaylist(playlistName = playlist.name)
        )
        else viewModelScope.launch {
            playlistsInteractor.addTrackToPlaylist(playlist, track)
            updatePlaylists()
            addTrackToPlaylistState.postValue(AddTrackToPlaylistState.TrackAdded(playlistName = playlist.name))
        }
    }

    private fun createUpdateTrackTimeTask() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlayerActive()) {
                delay(DELAY_FOR_UPDATE_TRACK_TIME)
                playerState.postValue(
                    PlayerState.Playing(
                        trackTime = playerInteractor.getCurrentPlayerPosition().toString()
                    )
                )
            }
        }
    }

    companion object {
        private const val DELAY_FOR_UPDATE_TRACK_TIME = 300L
    }
}