package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import com.practicum.playlistmaker.data.db.SearchHistory
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.dto.TracksRequest
import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.domain.Resource
import com.practicum.playlistmaker.domain.repository.TrackRepository
import com.practicum.playlistmaker.domain.model.Track
import java.lang.Exception

class TrackRepositoryImpl(private val networkClient: NetworkClient, private val sharedPrefs: SharedPreferences) : TrackRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> = try {
        val response = networkClient.doRequest(TracksRequest(expression))
        if (response is TracksResponse && response.resultCode == 200) {
            val trackList = response.results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.trackId,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
            Resource.Success(trackList)
        } else Resource.Error("Произошла сетевая ошибка")
    } catch (e: Exception) {
        Resource.Error("Произошла сетевая ошибка")
    }

    override fun saveTrackToHistory(track: Track) {
        SearchHistory(sharedPrefs).saveTrackToHistory(TrackDto(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl))
    }

    override fun getHistory(): Array<Track> =
        SearchHistory(sharedPrefs).getHistory().map {
            Track(
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.trackId,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }.toTypedArray()

    override fun clearHistory() {
        SearchHistory(sharedPrefs).clearHistory()
    }

    override fun doActionWhenTrackHistoryChanged(action: () -> Unit) {
        SearchHistory(sharedPrefs).doActionWhenTrackHistoryChanged(action)
    }
}