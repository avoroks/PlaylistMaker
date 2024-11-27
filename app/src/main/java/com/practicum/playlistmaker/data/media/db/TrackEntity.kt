package com.practicum.playlistmaker.data.media.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class TrackEntity(
    @PrimaryKey
    val id: Int,
    val imageUrl: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val duration: String,
    val previewUrl: String,
    val createdAt: Long
)