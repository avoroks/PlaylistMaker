package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.practicum.playlistmaker.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.domain.use_case.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.domain.use_case.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.use_case.TracksInteractorImpl

const val PLAYLIST_MAKER_PREFERENCES = "PLAYLIST_MAKER_PREFERENCES"

object Creator {
    private lateinit var context: Context
    fun setContext(context: Context) {
        this.context = context
    }

    private fun getSharedPreferences() = context.getSharedPreferences(
        PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE
    )

    private fun getTracksRepository() =
        TrackRepositoryImpl(RetrofitNetworkClient(), getSharedPreferences())

    fun provideTracksInteractor() = TracksInteractorImpl(getTracksRepository())

    private fun getMediaPlayerRepository() = MediaPlayerRepositoryImpl()
    fun provideMediaPlayerInteractor() = MediaPlayerInteractorImpl(getMediaPlayerRepository())

    private fun getSettingsRepository() = SettingsRepositoryImpl(getSharedPreferences())
    fun provideSettingsInteractor() = SettingsInteractorImpl(getSettingsRepository())
}