package com.kwapp.retrofit.pojo

import kotlinx.serialization.Serializable

@Serializable // ✅ Add this annotation
data class SearchHistoryItem(
    val displayName: String,
    val latitude: Double,
    val longitude: Double
)
