package com.metalstream.app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MetalStreamApiService {
    @GET("api/songs")
    suspend fun getSongs(): List<Song>
}

object NetworkClient {
    // 10.0.2.2 para emulador Android; muda para 192.168.1.70 se testares em telemóvel físico
    private const val BASE_URL = "http://10.0.2.2:5002/"

    val api: MetalStreamApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MetalStreamApiService::class.java)
    }
}