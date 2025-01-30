package com.kwapp.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current_weather") val currentWeather: CurrentWeather,
    @SerializedName("daily") val daily: Daily,
    @SerializedName("hourly") val hourly: Hourly
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

// ✅ Add missing hourly data
data class Hourly(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature2m: List<Double>,
    @SerializedName("apparent_temperature") val apparentTemperature: List<Double>,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: List<Double>,
    @SerializedName("weathercode") val weatherCode: List<Int>
)