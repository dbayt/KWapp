package com.kwapp.retrofit.service

import com.kwapp.retrofit.pojo.CityResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CityAutocompleteService {
    @GET("autocomplete")
    fun getCitySuggestions(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("apikey") apiKey: String
    ): Call<CityResponseWrapper>

    @GET("geocode")
    fun getCityGeocode(
        @Query("q") cityName: String,
        @Query("apikey") apiKey: String
    ): Call<CityResponseWrapper>
}
