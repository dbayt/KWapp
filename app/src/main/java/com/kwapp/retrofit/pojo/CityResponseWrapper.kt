package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class CityResponseWrapper(
    @SerializedName("items") val items: List<CityItem>? // Ensure this matches API response
)

data class CityItem(
    @SerializedName("title") val title: String, // API might use "title" instead of "name"
    @SerializedName("address") val address: AddressDetails? // Optional, if available
)

data class AddressDetails(
    @SerializedName("city") val city: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("postalCode") val postalCode: String?
)