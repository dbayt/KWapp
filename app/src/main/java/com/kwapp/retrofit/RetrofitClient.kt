package com.kwapp.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kwapp.retrofit.service.AddressApiService
import com.kwapp.retrofit.service.CityAutocompleteService
import com.kwapp.retrofit.service.GeocodeService
import com.kwapp.retrofit.service.WeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Base URLs
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/v1/"
    private const val HERE_AUTOCOMPLETE_BASE_URL = "https://autocomplete.search.hereapi.com/v1/"
//    private const val HERE_GEOCODE_BASE_URL = "https://geocode.search.hereapi.com/v1/"
    private const val OPENWEATHER_GEOCODE_BASE_URL = "https://api.openweathermap.org/"

    private const val ADDRESS_BASE_URL = "https://nominatim.openstreetmap.org/" // ✅ Address API

    // Gson Instance
    private val gson: Gson = GsonBuilder().setLenient().create()

    // OkHttp Client with Logging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun getRetrofitInstance(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // API Service Instances
    val weatherApi: WeatherApiService by lazy {
        getRetrofitInstance(WEATHER_BASE_URL).create(WeatherApiService::class.java)
    }

    val addressApi: AddressApiService by lazy { // ✅ Add Address API
        getRetrofitInstance(ADDRESS_BASE_URL).create(AddressApiService::class.java)
    }

    val cityAutocompleteApi: CityAutocompleteService by lazy {
        getRetrofitInstance(HERE_AUTOCOMPLETE_BASE_URL).create(CityAutocompleteService::class.java)
    }
    val geocodeApi: GeocodeService by lazy {
        getRetrofitInstance(OPENWEATHER_GEOCODE_BASE_URL).create(GeocodeService::class.java)
    }

}