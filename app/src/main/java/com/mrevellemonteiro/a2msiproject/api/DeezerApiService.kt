package com.mrevellemonteiro.a2msiproject.api

import com.mrevellemonteiro.a2msiproject.model.Track
import com.mrevellemonteiro.a2msiproject.model.SearchResponse
import com.mrevellemonteiro.a2msiproject.model.PlaylistTracksResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApiService {
    @GET("search/track")
    suspend fun searchTracks(@Query("q") query: String): Response<SearchResponse>

    @GET("track/{id}")
    suspend fun getTrack(@Path("id") id: Long): Response<Track>

    @GET("playlist/{id}/tracks")
    suspend fun getPlaylistTracks(@Path("id") playlistId: Long): Response<PlaylistTracksResponse>
}
