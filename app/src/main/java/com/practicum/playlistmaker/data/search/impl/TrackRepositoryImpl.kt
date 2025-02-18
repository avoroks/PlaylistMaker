package com.practicum.playlistmaker.data.search.impl

import com.practicum.playlistmaker.data.media.db.AppDatabase
import com.practicum.playlistmaker.data.search.sharedPrefs.SearchHistory
import com.practicum.playlistmaker.data.search.dto.TrackDto
import com.practicum.playlistmaker.data.search.dto.TracksRequest
import com.practicum.playlistmaker.data.search.dto.TracksResponse
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.domain.search.Resource
import com.practicum.playlistmaker.domain.search.repository.TrackRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory,
    private val db: AppDatabase
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
                            it.previewUrl,
                            false
                        )
                    }
                    val favoriteTracks = db.favoriteTrackDao().getIdTracks()

                    trackList.filter { it.trackId in favoriteTracks }
                        .map { it.copy(isFavorite = true) }

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

    override fun getHistory(): Flow<List<Track>> = flow {
        val tracks = searchHistory.getHistory().map {
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
                it.previewUrl,
                false
            )
        }.toList()

        val favoriteTracks = db.favoriteTrackDao().getIdTracks()

        tracks.filter { it.trackId in favoriteTracks }
            .map { it.copy(isFavorite = true) }

        emit(tracks)
    }

    override fun clearHistory() {
        searchHistory.clearHistory()
    }
}