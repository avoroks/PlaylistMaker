package com.practicum.playlistmaker.domain.search.use_case

import com.practicum.playlistmaker.domain.search.consumer.Consumer
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(
        expression: String
    ): Flow<Pair<List<Track>?, String?>>

    fun saveTrackToHistory(track: Track)
    fun getTrackHistory(): Flow<List<Track>>
    fun clearTrackHistory()
}