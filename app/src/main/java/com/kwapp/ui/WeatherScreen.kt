package com.kwapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.kwapp.R
import com.kwapp.retrofit.pojo.WeatherResponse
import com.kwapp.service.WeatherService
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun WeatherScreen(lifecycleOwner: LifecycleOwner) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val address = "Current Address: 123 Main Street"

    // ✅ Fix 1: Use collectAsStateWithLifecycle() properly
    val weatherData by WeatherService.weatherLiveData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color(0xFFF5F5DC)) // Creamy beige
    ) {
        // 🔹 Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search...") },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                // Handle search action
            })
        )

        // 🔹 Address TextView
        Text(
            text = address,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // 🔹 Show Loading State or Weather Data
        if (weatherData == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            val weatherList = weatherData!!.daily.time.mapIndexed { index: Int, time: String ->
                val maxTemp = weatherData!!.daily.temperatureMax[index]
                val minTemp = weatherData!!.daily.temperatureMin[index]

                // ✅ Approximate Feels Like Temperature (Average of Hourly Apparent Temperature)
                val feelsLike = weatherData!!.hourly.apparentTemperature
                    .subList(index * 24, (index + 1) * 24)
                    .average()
                    .toInt()

                val humidity = weatherData!!.hourly.relativeHumidity2m
                    .subList(index * 24, (index + 1) * 24)
                    .average()
                    .toInt()

                WeatherItem(
                    iconRes = R.drawable.ic_default, // TODO: Map weather code to icon
                    temperature = "$maxTemp°C", // ✅ Fix: Use Max Temp
                    feelsLike = "$feelsLike°C", // ✅ Fix: Use calculated Feels Like
                    minTemp = "$minTemp°C",
                    maxTemp = "$maxTemp°C",
                    humidity = "$humidity%", // ✅ Fix: Use calculated Humidity
                    dateInfo = time,
                    conditions = "Unknown" // TODO: Map weather code to description
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(weatherList) { weatherItem -> // ✅ weatherItem is now of type WeatherItem
                    WeatherItemView(weatherItem)
                }
            }
        }
    }
}

@Composable
fun WeatherItemView(weatherItem: WeatherItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF87CEFA)) // Sky Blue
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔹 Weather Icon
            Image(
                painter = painterResource(id = weatherItem.iconRes),
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 8.dp)
            )

            // 🔹 Weather Info Column
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Temp: ${weatherItem.temperature}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Feels Like: ${weatherItem.feelsLike}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Min: ${weatherItem.minTemp} | Max: ${weatherItem.maxTemp}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Humidity: ${weatherItem.humidity}", style = MaterialTheme.typography.bodyMedium)
            }

            // 🔹 Date & Conditions
            Column(horizontalAlignment = Alignment.End) {
                Text(text = weatherItem.dateInfo, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                Text(text = weatherItem.conditions, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            }
        }
    }
}

// ✅ Data Model for Weather Item
data class WeatherItem(
    val iconRes: Int,
    val temperature: String,
    val feelsLike: String,
    val minTemp: String,
    val maxTemp: String,
    val humidity: String,
    val dateInfo: String,
    val conditions: String
)