package com.kwapp.retrofit.service

import com.kwapp.retrofit.pojo.CityResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CityAutocompleteService {
    @GET("autocomplete")
    suspend fun getCitySuggestions(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5, // Max 5 items
        @Query("apikey") apiKey: String
    ): CityResponseWrapper

    @GET("geocode")
    suspend fun getCityGeocode(
        @Query("q") cityName: String,
        @Query("apikey") apiKey: String
    ): Call<CityResponseWrapper>
}
