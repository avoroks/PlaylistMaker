package com.practicum.playlistmaker.domain.search.repository

import com.practicum.playlistmaker.domain.search.Resource
import com.practicum.playlistmaker.domain.search.model.Track

interface TrackRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
    fun saveTrackToHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}