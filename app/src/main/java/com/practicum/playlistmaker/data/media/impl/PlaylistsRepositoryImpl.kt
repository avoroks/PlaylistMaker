package com.practicum.playlistmaker.data.media.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.media.db.AppDatabase
import com.practicum.playlistmaker.data.media.db.PlaylistEntity
import com.practicum.playlistmaker.data.media.db.TrackEntity
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.media.repository.PlaylistsRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(private val db: AppDatabase, private val gson: Gson) :
    PlaylistsRepository {
    override suspend fun addPlaylist(playlist: Playlist) =
        db.playlistDao().insertPlaylist(playlist.mapToPlaylistEntity())

    override suspend fun removePlaylist(playlist: Playlist) =
        db.playlistDao().deletePlaylist(playlist.mapToPlaylistEntity().id)

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = db.playlistDao().getPlaylists().map { it.mapToPlaylist() }
        emit(playlists)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val tracks: List<Int> = playlist.trackIds.let {
            val itemType = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(playlist.trackIds, itemType)
        } ?: emptyList()
        db.playlistDao().updatePlaylist(
            playlist.mapToPlaylistEntity().copy(
                trackIds = gson.toJson(tracks + track.trackId),
                trackCount = playlist.trackCount + 1
            )
        )
        db.trackDao().insertTrack(track.mapToTrackEntity())
    }

    private fun PlaylistEntity.mapToPlaylist() =
        Playlist(
            id = this.id,
            name = this.name,
            description = this.description,
            imageUrl = this.imageUrl,
            trackIds = this.trackIds,
            trackCount = this.trackCount
        )

    private fun Playlist.mapToPlaylistEntity() = PlaylistEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        trackIds = this.trackIds,
        trackCount = this.trackCount
    )

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