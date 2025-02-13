package com.practicum.playlistmaker.domain.media.use_case

import com.practicum.playlistmaker.domain.media.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(private val repository: FavoriteTracksRepository) :
    FavoriteTracksInteractor {
    override suspend fun addTrackToFavorite(track: Track) = repository.addTrackToFavorite(track)
    override suspend fun removeTrackFromFavorite(track: Track) =
        repository.removeTrackFromFavorite(track)

    override fun getFavoriteTracks(): Flow<List<Track>> = repository.getFavoriteTracks()
    override fun isTrackFavorite(track: Track): Flow<Boolean> = repository.isTrackFavorite(track)
}