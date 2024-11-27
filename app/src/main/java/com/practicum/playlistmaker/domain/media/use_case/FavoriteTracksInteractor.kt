package com.practicum.playlistmaker.domain.media.use_case

import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addTrackToFavorite(track: Track)
    suspend fun removeTrackFromFavorite(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}