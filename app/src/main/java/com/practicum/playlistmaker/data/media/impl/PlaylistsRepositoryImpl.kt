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

    override suspend fun removePlaylist(playlistId: Int) {
        val tracks = db.playlistDao().getPlaylistById(playlistId).trackIds

        db.playlistDao().deletePlaylist(playlistId)
        tracks.forEach {
            deleteTrackIfItNotInAnyPlaylist(it.code)
        }
    }

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

    override suspend fun updatePlaylistInfo(
        playlistId: Int,
        newName: String?,
        newDescription: String?,
        newImageUrl: String?
    ) {
        val playlist = db.playlistDao().getPlaylistById(playlistId)
        db.playlistDao().updatePlaylist(
            playlist.copy(
                name = newName ?: playlist.name,
                description = newDescription ?: playlist.description,
                imageUrl = newImageUrl ?: playlist.imageUrl
            )
        )
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int) {
        val playlist = db.playlistDao().getPlaylistById(playlistId)
        val tracks: List<Int> = playlist.trackIds.let {
            val itemType = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(it, itemType)
        } ?: emptyList()

        db.playlistDao().updatePlaylist(
            playlist.copy(
                trackIds = gson.toJson(tracks - trackId),
                trackCount = playlist.trackCount - 1
            )
        )

        deleteTrackIfItNotInAnyPlaylist(trackId)
    }

    private suspend fun deleteTrackIfItNotInAnyPlaylist(trackId: Int) {
        val allTrackIds = db.playlistDao().getAllTrackIdsPlaylists().flatMap {
            it.let {
                val itemType = object : TypeToken<List<Int>>() {}.type
                gson.fromJson<List<Int>>(it, itemType)
            } ?: emptyList()

        }

        if (!allTrackIds.contains(trackId)) db.trackDao()
            .deleteTrack(trackId)
    }


    override fun getPlaylistById(id: Int) = flow {
        val playlist = db.playlistDao().getPlaylistById(id).mapToPlaylist()
        emit(playlist)
    }

    override fun getPlaylistTracks(playlistId: Int): Flow<List<Track>> = flow {
        val tracks = db.trackDao().getTracks().filter {
            it.id.toString() in db.playlistDao().getPlaylistById(playlistId).trackIds
        }.mapToTrack()
        emit(tracks)
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
            previewUrl = it.previewUrl,
            isFavorite = false
        )
    }
}