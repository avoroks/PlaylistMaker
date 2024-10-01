package com.practicum.playlistmaker.di

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

    single<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
}