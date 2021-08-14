package com.example.demoride.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val  retrofit by lazy {
        Retrofit.Builder().baseUrl("https://maps.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build()
    }

    val api:MapService by lazy {
        retrofit.create(MapService::class.java)
    }
}