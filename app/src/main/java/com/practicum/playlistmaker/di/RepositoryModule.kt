package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.media.impl.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.data.player.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.impl.SharingRepositoryImpl
import com.practicum.playlistmaker.domain.media.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.player.repository.MediaPlayerRepository
import com.practicum.playlistmaker.domain.search.repository.TrackRepository
import com.practicum.playlistmaker.domain.settings.repository.SettingsRepository
import com.practicum.playlistmaker.domain.sharing.repository.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get(), get(), get())
    }

    single<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get())
    }
}