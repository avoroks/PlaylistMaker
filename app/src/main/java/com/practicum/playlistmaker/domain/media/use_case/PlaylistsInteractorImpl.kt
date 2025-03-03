package com.practicum.playlistmaker.domain.media.use_case

import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.media.repository.PlaylistsRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {
    override suspend fun addPlaylist(playlist: Playlist) = repository.addPlaylist(playlist)

    override suspend fun removePlaylist(playlistId: Int) = repository.removePlaylist(playlistId)

    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) =
        repository.addTrackToPlaylist(playlist, track)

    override fun getPlaylistById(id: Int): Flow<Playlist> = repository.getPlaylistById(id)

    override fun getPlaylistTracks(playlistId: Int): Flow<List<Track>> =
        repository.getPlaylistTracks(playlistId)

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int) =
        repository.deleteTrackFromPlaylist(playlistId, trackId)

    override suspend fun updatePlaylistInfo(playlistId: Int, newName: String?, newDescription: String?, newImageUrl: String?)
    = repository.updatePlaylistInfo(playlistId, newName, newDescription, newImageUrl)
}