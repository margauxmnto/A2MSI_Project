package com.mrevellemonteiro.a2msiproject.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DeezerApi {
    private const val BASE_URL = "https://api.deezer.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: DeezerApiService = retrofit.create(DeezerApiService::class.java)
}
