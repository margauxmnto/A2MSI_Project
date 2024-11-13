package com.mrevellemonteiro.a2msiproject

import com.mrevellemonteiro.a2msiproject.api.DeezerApi
import com.mrevellemonteiro.a2msiproject.model.Track

class DeezerRepository {
    suspend fun searchTracks(query: String): List<Track> {
        val response = DeezerApi.service.searchTracks(query)
        return if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            emptyList()
        }
    }
}