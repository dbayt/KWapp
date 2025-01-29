package com.kwapp.utils

import com.kwapp.R

enum class WeatherCondition(val minCode: Int, val maxCode: Int, val iconResId: Int) {
    CLEAR_SKY(0,0, R.drawable.sunny),
    SUNNY(0, 3, R.drawable.sunny),
    FOGGY(45, 48, R.drawable.foggy),
    DRIZZLE(51, 57, R.drawable.drizzle),
    RAINY(61, 67, R.drawable.rain),
    SNOWY(71, 77, R.drawable.snow),
    RAIN_SHOWERS(80, 82, R.drawable.rain_showers),
    SNOW_SHOWERS(85, 86, R.drawable.snow_showers),
    THUNDERSTORMS(95, 99, R.drawable.thunderstorms),
    DEFAULT(-1, -1, R.drawable.ic_default); // Default fallback icon

    companion object {
        fun fromCode(code: Int): WeatherCondition {
            return values().find { code in it.minCode..it.maxCode } ?: DEFAULT
        }
    }
}