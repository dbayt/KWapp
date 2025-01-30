package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class CityResponseWrapper(
    @SerializedName("items") val items: List<CityItem>?
)

data class CityItem(
    @SerializedName("title") val title: String,
    @SerializedName("address") val address: AddressDetails?
)

data class AddressDetails(
    @SerializedName("city") val city: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("postalCode") val postalCode: String?
)