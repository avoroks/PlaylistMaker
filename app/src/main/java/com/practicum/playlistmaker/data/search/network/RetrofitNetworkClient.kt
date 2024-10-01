package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.dto.Response
import com.practicum.playlistmaker.data.search.dto.TracksRequest

class RetrofitNetworkClient(private val trackService: ItunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        return if (dto is TracksRequest) {
            val resp = trackService.findTracks(dto.expression).execute()

            val body = resp.body() ?: Response()

            body.apply { resultCode = resp.code() }
        } else Response().apply { resultCode = 400 }
    }
}