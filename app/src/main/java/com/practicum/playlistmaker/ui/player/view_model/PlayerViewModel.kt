package com.practicum.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.player.use_case.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.player.state.PlayerState

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: MediaPlayerInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>()
    fun getPlayerState(): LiveData<PlayerState> = playerState

    private val handler = Handler(Looper.getMainLooper())

    init {
        playerInteractor.preparePlayer(
            track = track,
            onCompletionAction = {
                handler.removeCallbacks(createUpdateTrackTimeTask())
                playerState.value = PlayerState.TrackEnded

            }
        )
        playerState.value = PlayerState.Prepared
    }

    fun playBackControl() {
        playerInteractor.playbackControl(
            onStartAction = {
                handler.post(createUpdateTrackTimeTask())
            },
            onPauseAction = {
                handler.removeCallbacks(createUpdateTrackTimeTask())
                playerState.value = PlayerState.Paused
            }
        )
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerState.value = PlayerState.Paused
    }

    fun stopPlayer() {
        handler.removeCallbacks(createUpdateTrackTimeTask())
        playerInteractor.releaseResources()
    }

    private fun createUpdateTrackTimeTask() = object : Runnable {
        override fun run() {
            if (playerInteractor.isPlayerActive()) {
                playerState.postValue(
                    PlayerState.Playing(
                        trackTime = playerInteractor.getCurrentPlayerPosition().toString()
                    )
                )
                handler.postDelayed(this, DELAY_FOR_UPDATE_TRACK_TIME)
            }
        }
    }


    companion object {
        private const val DELAY_FOR_UPDATE_TRACK_TIME = 300L

        fun factory(track: Track): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    PlayerViewModel(
                        track,
                        Creator.provideMediaPlayerInteractor()
                    )
                }
            }
        }
    }
}