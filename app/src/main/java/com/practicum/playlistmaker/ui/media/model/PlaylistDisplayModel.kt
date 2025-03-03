package com.practicum.playlistmaker.ui.media.model

import com.practicum.playlistmaker.domain.search.model.Track

data class PlaylistDisplayModel(
    val name: String,
    val description: String,
    val imageUrl: String,
    val trackCount: String,
    val commonDuration: String,
    val tracks: List<Track>
)