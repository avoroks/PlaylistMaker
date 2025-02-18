package com.practicum.playlistmaker.domain.media.use_case

import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun addPlaylist(playlist: Playlist): Long
    suspend fun removePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
}