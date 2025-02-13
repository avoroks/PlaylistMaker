package com.practicum.playlistmaker.domain.search.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Locale

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String,
    var isFavorite: Boolean = false
) : Serializable {
    val humanReadableTrackTime: String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis.toLong())

    val releaseYear: String
        get() = releaseDate?.let { ZonedDateTime.parse(releaseDate).year.toString() } ?: ""

    val coverUrl: String
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}