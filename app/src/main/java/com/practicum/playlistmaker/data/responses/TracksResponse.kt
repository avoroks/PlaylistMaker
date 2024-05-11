package com.practicum.playlistmaker.data.responses

import com.practicum.playlistmaker.data.Track

data class TracksResponse(
    val resultCount: Int,
    val results: List<Track>
)