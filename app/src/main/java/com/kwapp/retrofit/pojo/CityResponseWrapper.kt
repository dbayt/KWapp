package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class CityResponseWrapper(
    @SerializedName("items") val items: List<CityResponse>?
)