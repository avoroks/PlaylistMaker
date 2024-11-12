package com.practicum.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.player.use_case.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.player.state.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    track: Track,
    private val playerInteractor: MediaPlayerInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>()
    fun getPlayerState(): LiveData<PlayerState> = playerState

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