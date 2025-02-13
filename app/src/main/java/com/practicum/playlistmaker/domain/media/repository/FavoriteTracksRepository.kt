package com.practicum.playlistmaker.domain.media.repository

import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addTrackToFavorite(track: Track)
    suspend fun removeTrackFromFavorite(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    fun isTrackFavorite(track: Track): Flow<Boolean>
}