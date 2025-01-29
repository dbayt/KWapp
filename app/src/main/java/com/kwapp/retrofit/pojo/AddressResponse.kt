package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class AddressResponse(
    @SerializedName("display_name") val displayName: String // ✅ Adjust this to match API response
)
