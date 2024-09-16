package com.practicum.playlistmaker.domain.use_case

import com.practicum.playlistmaker.domain.consumer.Consumer
import com.practicum.playlistmaker.domain.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer<List<Track>>)
    fun saveTrackToHistory(track: Track)
    fun getTrackHistory(): List<Track>
    fun clearTrackHistory()
    fun doActionWhenTrackHistoryChanged(action: () -> Unit)
}