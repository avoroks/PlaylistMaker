package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.dto.TracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song ")
    suspend fun findTracks(@Query("term") text: String): TracksResponse
}