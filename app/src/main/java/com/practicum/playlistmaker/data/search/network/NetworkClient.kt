package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}