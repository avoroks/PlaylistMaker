package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.media.use_case.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.media.use_case.FavoriteTracksInteractorImpl
import com.practicum.playlistmaker.domain.media.use_case.PlaylistsInteractor
import com.practicum.playlistmaker.domain.media.use_case.PlaylistsInteractorImpl
import com.practicum.playlistmaker.domain.player.use_case.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.player.use_case.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.domain.search.use_case.TracksInteractor
import com.practicum.playlistmaker.domain.search.use_case.TracksInteractorImpl
import com.practicum.playlistmaker.domain.settings.use_case.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.use_case.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.sharing.use_case.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.use_case.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }
    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }
}