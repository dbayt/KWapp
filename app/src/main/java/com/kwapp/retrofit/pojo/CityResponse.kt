package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class CityResponse(
    @SerializedName("name") val name: String // Ensure this matches the API response
)