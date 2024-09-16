package com.practicum.playlistmaker.data.dto

data class TracksResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()