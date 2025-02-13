package com.practicum.playlistmaker.domain.media.model

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String,
    val imageUrl: String,
    val trackIds: String = "",
    val trackCount: Int = 0
)