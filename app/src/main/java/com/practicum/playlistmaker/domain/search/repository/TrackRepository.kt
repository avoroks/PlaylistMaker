package com.practicum.playlistmaker.domain.search.repository

import com.practicum.playlistmaker.domain.search.Resource
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    fun saveTrackToHistory(track: Track)
    fun getHistory(): Flow<List<Track>>
    fun clearHistory()
}