package com.kwapp.retrofit.service

import com.kwapp.retrofit.pojo.GeocodeResponseItem
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeService {
    @GET("geo/1.0/direct")
    suspend fun getCityCoordinates(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 1, // We only need the first result
        @Query("appid") apiKey: String = "07a574617ecd8549de8790e3d5b94ac2" //Private key
    ): List<GeocodeResponseItem>
}