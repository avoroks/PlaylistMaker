package com.practicum.playlistmaker.data

import java.text.SimpleDateFormat
import java.util.Locale

data class Track(val trackName: String, val artistName: String, val trackTimeMillis: String, val artworkUrl100: String) {
    val humanReadableTrackTime: String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis.toLong())
}