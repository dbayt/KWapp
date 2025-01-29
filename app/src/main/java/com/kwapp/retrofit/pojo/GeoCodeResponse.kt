package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class GeocodeResponseItem(
    @SerializedName("name") val name: String,   // City name
    @SerializedName("lat") val latitude: Double, // Latitude
    @SerializedName("lon") val longitude: Double, // Longitude
    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String?
)