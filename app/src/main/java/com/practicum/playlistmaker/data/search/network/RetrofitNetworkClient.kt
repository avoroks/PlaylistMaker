package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.dto.Response
import com.practicum.playlistmaker.data.search.dto.TracksRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val trackService: ItunesApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response = if (dto is TracksRequest)
        withContext(Dispatchers.IO) {
            try {
                val resp = trackService.findTracks(dto.expression)
                resp.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        } else Response().apply { resultCode = 400 }
}
