package com.practicum.playlistmaker.data.media.impl

import com.practicum.playlistmaker.data.media.db.AppDatabase
import com.practicum.playlistmaker.data.media.db.FavoriteTrackEntity
import com.practicum.playlistmaker.domain.media.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(private val db: AppDatabase) : FavoriteTracksRepository {
    override suspend fun addTrackToFavorite(track: Track) =
        db.favoriteTrackDao().insertTrack(track.mapToTrackEntity())

    override suspend fun removeTrackFromFavorite(track: Track) =
        db.favoriteTrackDao().deleteTrack(track.mapToTrackEntity().id)

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = db.favoriteTrackDao().getTracks().mapToTrack()
        emit(tracks)
    }

    override fun isTrackFavorite(track: Track): Flow<Boolean> = flow {
        val tracks = db.favoriteTrackDao().getTracks().mapToTrack()
        val count = tracks.count { it.trackId == track.trackId }
        emit(count > 0)
    }

    private fun List<FavoriteTrackEntity>.mapToTrack() = map {
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
            previewUrl = it.previewUrl,
            isFavorite = true
        )
    }

    private fun Track.mapToTrackEntity() = FavoriteTrackEntity(
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