package com.practicum.playlistmaker.domain.media.use_case

import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun addPlaylist(playlist: Playlist): Long
    suspend fun removePlaylist(playlistId: Int)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
    fun getPlaylistById(id: Int): Flow<Playlist>
    fun getPlaylistTracks(playlistId: Int): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int)
    suspend fun updatePlaylistInfo(playlistId: Int, newName: String? = null, newDescription: String? = null, newImageUrl: String? = null)
}