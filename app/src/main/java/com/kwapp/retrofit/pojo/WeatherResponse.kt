package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current_weather") val currentWeather: CurrentWeather,
    @SerializedName("daily") val daily: Daily
)

data class CurrentWeather(
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("weathercode") val weatherCode: Int,
    @SerializedName("relative_humidity_2m") val relativeHumidity: Double
)

data class Daily(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double>,
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerializedName("weathercode") val weatherCode: List<Int>
)