package com.kwapp.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwapp.R
import com.kwapp.retrofit.pojo.CityItem
import com.kwapp.retrofit.pojo.SearchHistoryItem
import com.kwapp.service.WeatherService
import com.kwapp.utils.DateUtils
import com.kwapp.utils.WeatherCondition
import com.kwapp.utils.SearchHistoryManager
import com.kwapp.utils.TAG
import kotlinx.coroutines.launch

    @Composable
    fun WeatherScreen(lifecycleOwner: LifecycleOwner) {
        val context = LocalContext.current // Get Context inside Composable

        var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

        val coroutineScope = rememberCoroutineScope()

        // Observe Address and Weather Data, City suggestions
        var citySuggestionsList by remember { mutableStateOf(listOf<CityItem>()) } // Track city suggestions
        val address by WeatherService.addressLiveData.collectAsStateWithLifecycle() // Address Flow
        val weatherData by WeatherService.weatherLiveData.collectAsStateWithLifecycle() // Weather Flow

        val selectedCoordinates by WeatherService.selectedCoordinatesFlow.collectAsStateWithLifecycle()
        var searchHistory by remember { mutableStateOf(listOf<SearchHistoryItem>()) } // Store last 5 searches
        var showHistory by remember { mutableStateOf(false) } // Controls history visibility
        val weatherService = WeatherService() // Create an instance

        val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
        val searchHistoryManager = remember { SearchHistoryManager(context) }

        // Update city suggestions list from service
        val citySuggestions by WeatherService.citySuggestionsLiveData.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            searchHistoryManager.searchHistory.collect { history ->
                searchHistory = history
            }
        }


        LaunchedEffect(citySuggestions) {
            citySuggestionsList = citySuggestions
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(color = Color(0xFFF5F5DC)) // Creamy beige
        ) {
                // 🔹 Show Search Bar only if it's not collapsed
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    showHistory = query.text.isEmpty() // Show history only when search bar is empty
                    if (query.text.isNotEmpty()) {
                        WeatherService().fetchCitySuggestions(query.text) // Fetch city suggestions
                    }
                },
                placeholder = { Text("Search city...") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { showHistory = true }, // Clicking shows history
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(id = R.color.sky_blue),
                    unfocusedContainerColor = colorResource(id = R.color.sky_blue),
                    disabledContainerColor = colorResource(id = R.color.sky_blue)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide() // Close keyboard
                })
            )



            // 🔹 Combined List: Show either History or Suggestions
            LazyColumn(modifier = Modifier.fillMaxWidth()) {

                //History
                if (showHistory && citySuggestionsList.isEmpty()) {
                    items(searchHistory) { historyItem ->
                        Text(
                            text = historyItem.displayName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(Color(0xFF87CEFA))
                                .clickable {
                                    // Hide keyboard & update query
                                    searchQuery = TextFieldValue("")
                                    keyboardController?.hide()
                                    showHistory = false

                                    // Update Weather Data for selected location
                                    weatherService.fetchWeatherAndAddress(historyItem.latitude, historyItem.longitude)

                                }

                        )
                    }
                } else {
                    //Autocomplete suggestions
                    items(citySuggestionsList) { cityItem ->
                        Text(
                            text = cityItem.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(Color.LightGray)
                                .clickable {
                                    // Clear search query & hide keyboard
                                    searchQuery = TextFieldValue("")
                                    keyboardController?.hide()
                                    citySuggestionsList = emptyList()

                                    val cityName = cityItem.address?.city ?: "Unknown City"

                                    // Fetch City Coordinates and Update Weather
                                    Log.i(TAG," CITYINAMEDEBUGLOG: " + cityName)
                                    weatherService.fetchCityCoordinates(cityName)

                                    // Save the confirmed city in history
                                    weatherService.getCityDetails(cityName) { displayName, lat, lon ->
                                        val newSearch = SearchHistoryItem(displayName, lat, lon)
                                        val updatedHistory = (listOf(newSearch) + searchHistory).take(5)

                                        searchHistory = updatedHistory
                                        coroutineScope.launch {
                                            searchHistoryManager.saveSearchHistory(updatedHistory)
                                        }

                                    }
                                }
                        )
                    }
                }
            }

            // Address TextView (Shows the received address dynamically)
            Text(
                text = address.orEmpty(), // Converts null to ""
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            //Weather list
            // Show Loading State or Weather Data
            if (weatherData == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                val weatherList = weatherData!!.daily.time.mapIndexed { index: Int, time: String ->
                    val maxTemp = weatherData!!.daily.temperatureMax[index]
                    val minTemp = weatherData!!.daily.temperatureMin[index]

                    // Approximate Feels Like Temperature (Average of Hourly Apparent Temperature)
                    val feelsLike = weatherData!!.hourly.apparentTemperature
                        .subList(index * 24, (index + 1) * 24)
                        .average()
                        .toInt()

                    val humidity = weatherData!!.hourly.relativeHumidity2m
                        .subList(index * 24, (index + 1) * 24)
                        .average()
                        .toInt()

                    val displayDate = if (index == 0) {
                        context.getString(R.string.today) // Show "Today"
                    } else {
                        DateUtils.formatDate(time) // Convert "yyyy-MM-dd" -> "d.M.yyyy"
                    }

                    WeatherItem(
                        iconRes = WeatherCondition.fromCode(weatherData!!.daily.weatherCode[index]).iconResId, // Fixed icon mapping
                        temperature = "$maxTemp°C",
                        feelsLike = "$feelsLike°C",
                        minTemp = "$minTemp°C",
                        maxTemp = "$maxTemp°C",
                        humidity = "$humidity%",
                        dateInfo = displayDate,
                        weatherCode = weatherData!!.daily.weatherCode[index]
                    )
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(weatherList) { weatherItem ->
                        WeatherItemView(weatherItem)
                    }
                }
            }
        }
    }

    @Composable
    fun WeatherItemView(weatherItem: WeatherItem) {
        val weatherCondition = WeatherCondition.fromCode(weatherItem.weatherCode)



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
                // Weather Icon (Fixed)
                Image(
                    painter = painterResource(id = weatherCondition.iconResId),
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
                    val context = LocalContext.current
                    Text(
                        text = buildAnnotatedString {
                            if (weatherItem.dateInfo == context.getString(R.string.today)) {
                                pushStyle(SpanStyle(fontWeight = FontWeight.Bold)) // Make "Today" Bold
                                append(weatherItem.dateInfo)
                                pop()
                            } else {
                                append(weatherItem.dateInfo) // Normal text for other dates
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )

                    Text(
                    text = weatherCondition.name.replace("_", " "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        }
    }
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
    val weatherCode: Int
)
