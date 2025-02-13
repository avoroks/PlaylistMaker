package com.practicum.playlistmaker.domain.media.use_case

import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.media.repository.PlaylistsRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {
    override suspend fun addPlaylist(playlist: Playlist) = repository.addPlaylist(playlist)

    override suspend fun removePlaylist(playlist: Playlist) = repository.removePlaylist(playlist)

    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) =
        repository.addTrackToPlaylist(playlist, track)
}