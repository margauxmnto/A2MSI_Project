package com.mrevellemonteiro.a2msiproject.api

import com.mrevellemonteiro.a2msiproject.model.SearchResponse
import com.mrevellemonteiro.a2msiproject.model.Track
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface DeezerApiService {
    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): Response<SearchResponse>

    @GET("track/{id}")
    suspend fun getTrack(@Path("id") id: Long): Response<Track>
}