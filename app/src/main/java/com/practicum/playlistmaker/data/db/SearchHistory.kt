package com.practicum.playlistmaker.data.db

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.TrackDto
import java.util.LinkedList

internal const val TRACK_HISTORY = "track_history"

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    fun saveTrackToHistory(track: TrackDto) {
        val trackList = LinkedList(getHistory().toMutableList())

        trackList.remove((trackList.firstOrNull { it.trackId == track.trackId }))
        trackList.push(track)

        sharedPreferences.edit()
            .putString(TRACK_HISTORY, Gson().toJson(trackList.take(10)))
            .apply()
    }

    fun getHistory(): Array<TrackDto> {
        val json = sharedPreferences.getString(TRACK_HISTORY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<TrackDto>::class.java)
    }

    fun clearHistory() = sharedPreferences.edit().remove(TRACK_HISTORY).apply()

    fun doActionWhenTrackHistoryChanged(action: () -> Unit) {
        val listener = OnSharedPreferenceChangeListener { _, key ->
            if (key == TRACK_HISTORY) {
                action()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }
}