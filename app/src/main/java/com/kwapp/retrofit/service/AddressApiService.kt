package com.kwapp.retrofit.service

import com.kwapp.retrofit.pojo.AddressResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AddressApiService {
    @GET("reverse")
    fun getAddress(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json",
        @Query("apikey") apiKey: String = "wgNGnsqyPfnyY5NVTEUNx89qYvpf5Hu2mV34tXzDWVQ" //Private key
    ): Call<AddressResponse>
}