package com.practicum.playlistmaker.data.search.impl

import com.practicum.playlistmaker.data.search.db.SearchHistory
import com.practicum.playlistmaker.data.search.dto.TrackDto
import com.practicum.playlistmaker.data.search.dto.TracksRequest
import com.practicum.playlistmaker.data.search.dto.TracksResponse
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.domain.search.Resource
import com.practicum.playlistmaker.domain.search.repository.TrackRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import java.util.concurrent.Flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory
) :
    TrackRepository {
    override fun searchTracks(expression: String) = flow {

        val response = networkClient.doRequest(TracksRequest(expression))

        when (response.resultCode) {
            200 -> {
                with(response as TracksResponse) {
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
                    emit(Resource.Success(trackList))
                }
            }
            else -> {
                emit(Resource.Error("Произошла сетевая ошибка"))
            }
        }
    }

    override fun saveTrackToHistory(track: Track) {
        searchHistory.saveTrackToHistory(
            TrackDto(
                track.trackName,
                track.artistName,
                track.trackTimeMillis,
                track.artworkUrl100,
                track.trackId,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl
            )
        )
    }

    override fun getHistory(): List<Track> =
        searchHistory.getHistory().map {
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
        }.toList()

    override fun clearHistory() {
        searchHistory.clearHistory()
    }
}