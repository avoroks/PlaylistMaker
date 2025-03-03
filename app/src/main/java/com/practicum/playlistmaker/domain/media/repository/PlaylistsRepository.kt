package com.practicum.playlistmaker.domain.media.repository

import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun addPlaylist(playlist: Playlist): Long
    suspend fun removePlaylist(playlistId: Int)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
    suspend fun updatePlaylistInfo(playlistId: Int, newName: String? = null, newDescription: String? = null, newImageUrl: String? = null)
    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int)
    fun getPlaylistById(id: Int): Flow<Playlist>
    fun getPlaylistTracks(playlistId: Int): Flow<List<Track>>
}