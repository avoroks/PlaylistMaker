package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.Resource
import com.practicum.playlistmaker.domain.model.Track

interface TrackRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
    fun saveTrackToHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
    fun doActionWhenTrackHistoryChanged(action: () -> Unit)
}