package com.practicum.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.media.use_case.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.player.use_case.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.player.state.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    val track: Track,
    private val playerInteractor: MediaPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>()
    fun getPlayerState(): LiveData<PlayerState> = playerState

    private val isTrackAddedToFavoriteState = MutableLiveData<Boolean>()
    fun getIsTrackAddedToFavoriteState(): LiveData<Boolean> = isTrackAddedToFavoriteState

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
        isTrackAddedToFavoriteState.value = track.isFavorite
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
            if (track.isFavorite) favoriteTracksInteractor.removeTrackFromFavorite(track)
            else favoriteTracksInteractor.addTrackToFavorite(track)
        }
        track.isFavorite = !track.isFavorite
        isTrackAddedToFavoriteState.value = track.isFavorite
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