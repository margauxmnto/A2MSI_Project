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

    suspend fun getTrack(id: Long): Track? {
        val response = DeezerApi.service.getTrack(id)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}