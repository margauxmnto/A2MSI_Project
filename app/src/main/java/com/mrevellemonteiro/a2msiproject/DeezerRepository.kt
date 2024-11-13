package com.mrevellemonteiro.a2msiproject

import com.mrevellemonteiro.a2msiproject.api.DeezerApi
import com.mrevellemonteiro.a2msiproject.model.Track
import android.util.Log

class DeezerRepository {
    suspend fun searchTracks(query: String): List<Track> {
        return try {
            val response = DeezerApi.service.searchTracks(query)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("DeezerRepository", "Error searching tracks: ${e.message}")
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