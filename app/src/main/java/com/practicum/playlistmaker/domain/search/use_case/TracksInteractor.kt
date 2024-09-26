package com.practicum.playlistmaker.domain.search.use_case

import com.practicum.playlistmaker.domain.search.consumer.Consumer
import com.practicum.playlistmaker.domain.search.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer<List<Track>>)
    fun saveTrackToHistory(track: Track)
    fun getTrackHistory(): List<Track>
    fun clearTrackHistory()
}