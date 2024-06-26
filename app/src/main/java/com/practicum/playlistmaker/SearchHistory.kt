package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.Track
import java.util.LinkedList

internal const val TRACK_HISTORY = "track_history"

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    fun saveTrackToHistory(track: Track) {
        val trackList = LinkedList(getHistory().toMutableList())

        trackList.remove((trackList.firstOrNull { it.trackId == track.trackId }))
        trackList.push(track)

        sharedPreferences.edit()
            .putString(TRACK_HISTORY, Gson().toJson(trackList.take(10)))
            .apply()
    }

    fun getHistory(): Array<Track> {
        val json = sharedPreferences.getString(TRACK_HISTORY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    fun clearHistory() = sharedPreferences.edit().remove(TRACK_HISTORY).apply()
}