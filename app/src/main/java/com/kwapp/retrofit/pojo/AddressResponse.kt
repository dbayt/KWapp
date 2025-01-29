package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class AddressResponse(
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("address") val address: Address?
) {
    data class Address(
        @SerializedName("house_number") val houseNumber: String?,
        @SerializedName("road") val road: String?,
        @SerializedName("village") val village: String?,
        @SerializedName("town") val town: String?,
        @SerializedName("postcode") val postcode: String?,
        @SerializedName("country") val country: String?,
        @SerializedName("country_code") val countryCode: String?
    )
}