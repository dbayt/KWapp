package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class CityResponse(
    @SerializedName("title") val name: String,
    @SerializedName("position") val position: Position? // Nullable since it can be null
) {
    data class Position(
        @SerializedName("lat") val latitude: Double?,  // Nullable
        @SerializedName("lng") val longitude: Double? // Nullable
    )
}