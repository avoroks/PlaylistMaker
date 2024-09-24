package com.practicum.playlistmaker.data.search.dto

data class TracksResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()