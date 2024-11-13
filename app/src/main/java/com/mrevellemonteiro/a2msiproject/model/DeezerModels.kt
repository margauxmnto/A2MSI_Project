package com.mrevellemonteiro.a2msiproject.model

data class SearchResponse(
    val data: List<Track>
)

data class Track(
    val id: Long,
    val title: String,
    val artist: Artist,
    val album: Album,
    val preview: String
)

data class Artist(
    val name: String
)

data class Album(
    val title: String
)