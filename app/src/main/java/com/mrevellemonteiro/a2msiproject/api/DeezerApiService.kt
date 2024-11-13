package com.mrevellemonteiro.a2msiproject.api

import com.mrevellemonteiro.a2msiproject.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApiService {
    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): Response<SearchResponse>
}