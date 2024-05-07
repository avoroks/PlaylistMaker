package com.practicum.playlistmaker

import com.practicum.playlistmaker.data.responses.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song ")
    fun findTracks(@Query("term") text: String): Call<TracksResponse>
}