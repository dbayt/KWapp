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
import com.kwapp.R

@Composable
fun WeatherScreen() {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val address = "Current Address: 123 Main Street"
    val weatherItems = listOf(
        WeatherItem(
            iconRes = R.drawable.ic_default,
            temperature = "25°C",
            feelsLike = "22°C",
            minTemp = "18°C",
            maxTemp = "30°C",
            humidity = "75%",
            dateInfo = "2024-06-01",
            conditions = "Sunny"
        ),
        WeatherItem(
            iconRes = R.drawable.ic_default,
            temperature = "20°C",
            feelsLike = "18°C",
            minTemp = "15°C",
            maxTemp = "23°C",
            humidity = "80%",
            dateInfo = "2024-06-02",
            conditions = "Cloudy"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color(0xFFF5F5DC)) // Creamy beige
    ) {
        // Search Bar
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

        // Address TextView
        Text(
            text = address,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // LazyColumn for Weather Items
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(weatherItems) { weatherItem ->
                WeatherItemView(weatherItem)
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
            // Weather Icon
            Image(
                painter = painterResource(id = weatherItem.iconRes),
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 8.dp)
            )

            // Weather Info Column
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Temp: ${weatherItem.temperature}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Feels Like: ${weatherItem.feelsLike}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Min: ${weatherItem.minTemp} | Max: ${weatherItem.maxTemp}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Humidity: ${weatherItem.humidity}", style = MaterialTheme.typography.bodyMedium)
            }

            // Date & Conditions
            Column(horizontalAlignment = Alignment.End) {
                Text(text = weatherItem.dateInfo, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                Text(text = weatherItem.conditions, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    WeatherScreen()
}

// Data Model for Weather Item
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
