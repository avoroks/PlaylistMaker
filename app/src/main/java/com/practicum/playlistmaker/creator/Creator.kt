package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.practicum.playlistmaker.data.search.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.player.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.domain.player.use_case.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.domain.settings.use_case.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.search.use_case.TracksInteractorImpl
import com.practicum.playlistmaker.domain.sharing.use_case.SharingInteractorImpl

const val PLAYLIST_MAKER_PREFERENCES = "PLAYLIST_MAKER_PREFERENCES"

object Creator {
    private lateinit var application: Application
    fun setApp(application: Application) {
        this.application = application
    }

    private fun getSharedPreferences() = application.getSharedPreferences(
        PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE
    )

    private fun getTracksRepository() =
        TrackRepositoryImpl(RetrofitNetworkClient(), getSharedPreferences())
    fun provideTracksInteractor() = TracksInteractorImpl(getTracksRepository())

    private fun getMediaPlayerRepository() = MediaPlayerRepositoryImpl()
    fun provideMediaPlayerInteractor() = MediaPlayerInteractorImpl(getMediaPlayerRepository())

    private fun getSettingsRepository() = SettingsRepositoryImpl(getSharedPreferences())
    fun provideSettingsInteractor() = SettingsInteractorImpl(getSettingsRepository())

    private fun getSharingRepository() = SharingRepositoryImpl(application)
    fun provideSharingInteractor() = SharingInteractorImpl(getSharingRepository())
}