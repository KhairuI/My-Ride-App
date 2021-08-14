package com.example.demoride.api

import com.example.demoride.model.Prediction
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface MapService {

    @GET
   suspend fun getPlaceList(@Url url: String): Response<ResponseBody>

    @GET
    suspend fun getPlace(@Url url: String): Response<ResponseBody>

    @GET
    suspend fun getRoute(@Url url: String): Response<ResponseBody>

    @GET
    suspend fun getNearbyPlace(@Url url: String): Response<ResponseBody>

}