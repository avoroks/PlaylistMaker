package com.practicum.playlistmaker.data.media.impl

import com.practicum.playlistmaker.data.media.db.AppDatabase
import com.practicum.playlistmaker.data.media.db.TrackEntity
import com.practicum.playlistmaker.domain.media.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(private val db: AppDatabase) : FavoriteTracksRepository {
    override suspend fun addTrackToFavorite(track: Track) =
        db.trackDao().insertTrack(track.mapToTrackEntity())

    override suspend fun removeTrackFromFavorite(track: Track) =
        db.trackDao().deleteTrack(track.mapToTrackEntity().id)

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = db.trackDao().getTracks().mapToTrack()
        tracks.forEach { it.isFavorite = true }
        emit(tracks)
    }

    private fun List<TrackEntity>.mapToTrack() = map {
        Track(
            trackName = it.trackName,
            artistName = it.artistName,
            trackTimeMillis = it.duration,
            artworkUrl100 = it.imageUrl,
            trackId = it.id,
            collectionName = it.collectionName,
            releaseDate = it.releaseDate,
            primaryGenreName = it.primaryGenreName,
            country = it.country,
            previewUrl = it.previewUrl
        )
    }

    private fun Track.mapToTrackEntity() = TrackEntity(
        id = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        duration = this.trackTimeMillis,
        imageUrl = this.artworkUrl100,
        collectionName = this.collectionName ?: "",
        releaseDate = this.releaseDate ?: "",
        primaryGenreName = this.primaryGenreName ?: "",
        country = this.country ?: "",
        previewUrl = this.previewUrl,
        createdAt = System.currentTimeMillis()
    )
}