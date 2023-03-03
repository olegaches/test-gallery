package com.example.imagesproject.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface ImagesApi {

    @GET("images.txt")
    @Streaming
    suspend fun loadFile(): ResponseBody

    companion object {
        const val BASE_URL = "https://it-link.ru/test/"
    }
}