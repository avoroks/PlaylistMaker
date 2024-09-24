package com.practicum.playlistmaker.data.search.db

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.search.dto.TrackDto
import java.util.LinkedList

internal const val TRACK_HISTORY = "track_history"

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()

    fun saveTrackToHistory(track: TrackDto) {
        val trackList = LinkedList(getHistory().toMutableList())

        trackList.remove((trackList.firstOrNull { it.trackId == track.trackId }))
        trackList.push(track)

        sharedPreferences.edit()
            .putString(TRACK_HISTORY, gson.toJson(trackList.take(10)))
            .apply()
    }

    fun getHistory(): List<TrackDto> {
        val json = sharedPreferences.getString(TRACK_HISTORY, null) ?: return listOf()
        val itemType = object : TypeToken<List<TrackDto>>() {}.type
        return gson.fromJson(json, itemType)
    }

    fun clearHistory() = sharedPreferences.edit().remove(TRACK_HISTORY).apply()
}