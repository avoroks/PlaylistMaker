package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.view_model.EditPlaylistViewModel
import com.practicum.playlistmaker.ui.media.view_model.FavoriteViewModel
import com.practicum.playlistmaker.ui.media.view_model.PlaylistViewModel
import com.practicum.playlistmaker.ui.media.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get(), get())
    }

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get(), get())
    }

    viewModel {
        FavoriteViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel { (playlistId: Int) ->
        PlaylistViewModel(playlistId, get())
    }

    viewModel {(playlistId: Int) ->
        EditPlaylistViewModel(playlistId, get())
    }
}