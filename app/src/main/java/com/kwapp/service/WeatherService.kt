package com.kwapp.service


import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.kwapp.retrofit.RetrofitClient
import com.kwapp.retrofit.RetrofitClient.addressApi
import com.kwapp.retrofit.pojo.AddressResponse
import com.kwapp.retrofit.pojo.WeatherResponse
import com.kwapp.retrofit.service.AddressApiService
import com.kwapp.retrofit.service.CityAutocompleteService
import com.kwapp.utils.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherService : Service() {


    companion object {
        private val _currentLocationFlow = MutableStateFlow<Location?>(null)
        val currentLocationFlow = _currentLocationFlow.asStateFlow()

        private val _weatherFlow = MutableStateFlow<WeatherResponse?>(null)
        val weatherLiveData = _weatherFlow.asStateFlow()

        private val _addressFlow = MutableStateFlow<String?>(null) // ✅ Store address
        val addressLiveData = _addressFlow.asStateFlow()

        private val _citySuggestionsFlow = MutableStateFlow<List<String>>(emptyList()) // ✅ Store City Suggestions
        val citySuggestionsLiveData = _citySuggestionsFlow.asStateFlow()

        private val _selectedCoordinatesFlow = MutableStateFlow<Pair<Double, Double>?>(null)
        val selectedCoordinatesFlow = _selectedCoordinatesFlow.asStateFlow()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastSavedLocation: Location? = null
    private val addressApi: AddressApiService = RetrofitClient.addressApi // ✅ Fix: Use Address API from RetrofitClient
    private val cityAutocompleteApi: CityAutocompleteService = RetrofitClient.cityAutocompleteApi


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Created")

        //When we get location, we fetch address and weather

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d(TAG, "On location result")

                super.onLocationResult(locationResult)
                val lastLocation = locationResult.lastLocation

                if (lastLocation != null) {
                    val previousLocation = lastSavedLocation

                    if (previousLocation == null ||
                        kotlin.math.abs(lastLocation.latitude - previousLocation.latitude) > 0.001 ||
                        kotlin.math.abs(lastLocation.longitude - previousLocation.longitude) > 0.001) {

                        Log.i(TAG, "New significant location update: Lat=${lastLocation.latitude}, Lon=${lastLocation.longitude}")

                        _currentLocationFlow.value = lastLocation
                        lastSavedLocation = lastLocation

                        // ✅ Fetch Weather & Address
                        fetchWeatherAndAddress(lastLocation.latitude, lastLocation.longitude)

                    } else {
                        Log.d(TAG, "Location update ignored (change < 0.001)")
                    }
                } else {
                    Log.w(TAG, "Received null location")
                }
            }
        }

        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.i(TAG, "Immediate location: Lat=${location.latitude}, Lon=${location.longitude}")
                    _currentLocationFlow.value = location
                    fetchWeatherAndAddress(location.latitude, location.longitude)
                } else {
                    Log.w(TAG, "Immediate location is null")
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to get immediate location", e)
            }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Starting continuous location updates")
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
        } else {
            Log.w(TAG, "Location permission not granted, cannot start updates")
        }
    }

    // ✅ Combined Function to Fetch Both Weather & Address
    fun fetchWeatherAndAddress(lat: Double, lon: Double) {
        fetchWeatherData(lat, lon) { success ->
            if (success) {
                fetchAddressData(lat, lon)
            }
        }
    }

    private fun fetchWeatherData(lat: Double, lon: Double, callback: (Boolean) -> Unit) {
        Log.d(TAG, "🌍 Fetching weather data for Lat: $lat, Lon: $lon")

        val call = RetrofitClient.weatherApi.getWeather(lat, lon)
        Log.d(TAG, "🌍 Sending Weather API request: $call")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                Log.d(TAG, "✅ Weather API Response received")

                if (response.isSuccessful) {
                    Log.d(TAG, "🌤️ Success: ${response.body()}")
                    _weatherFlow.value = response.body()
                    callback(true) // ✅ Weather fetched successfully
                } else {
                    Log.e(TAG, "❌ API Error: ${response.code()} - ${response.errorBody()?.string()}")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e(TAG, "🚨 Weather API Call Failed", t)
                callback(false)
            }
        })
    }

    private fun fetchAddressData(lat: Double, lon: Double) {
        Log.d(TAG, "📍 Fetching address for Lat: $lat, Lon: $lon")
        val call = addressApi.getAddress(lat, lon) // ✅ Replace with actual API key
        call.enqueue(object : Callback<AddressResponse> {
            override fun onResponse(call: Call<AddressResponse>, response: Response<AddressResponse>) {
                if (response.isSuccessful) {
                    val address = response.body()?.displayName ?: "Unknown Location"
                    Log.d(TAG, "📍 Address found: $address")
                    _addressFlow.value = address
                } else {
                    Log.e(TAG, "❌ Address API Error: ${response.code()} - ${response.errorBody()?.string()}")
                    _addressFlow.value = "Unknown Location"
                }
            }

            override fun onFailure(call: Call<AddressResponse>, t: Throwable) {
                Log.e(TAG, "🚨 Address API Call Failed", t)
                _addressFlow.value = "Unknown Location"
            }
        })
    }
    fun fetchCitySuggestions(query: String) {
        if (query.length < 2) {
            _citySuggestionsFlow.value = emptyList()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.cityAutocompleteApi.getCitySuggestions(query, 5)

                val cityTitles = response.items
                    ?.mapNotNull { it.title } // ✅ Extract title directly
                    ?.distinct() // ✅ Ensure unique suggestions
                    ?: emptyList()

                _citySuggestionsFlow.value = cityTitles
                Log.d(TAG, "✅ City Suggestions: $cityTitles")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to fetch city suggestions", e)
                _citySuggestionsFlow.value = emptyList()
            }
        }
    }
    fun fetchCityCoordinates(cityName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "✅ Fetching coordinates for city: $cityName")
                val response = RetrofitClient.geocodeApi.getCityCoordinates(cityName, 1)
                if (response.isNotEmpty()) {
                    val location = response.first()
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // ✅ Update selected coordinates
                    _selectedCoordinatesFlow.value = Pair(latitude, longitude)
                    Log.d(TAG, "✅ City coordinates: $latitude, $longitude")

                    // ✅ Fetch weather after successfully fetching coordinates
                    fetchWeatherAndAddress(latitude, longitude)

                } else {
                    Log.e(TAG, "❌ No coordinates found for $cityName")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to fetch city coordinates", e)
            }
        }
    }

    fun getCityDetails(cityName: String, callback: (String, Double, Double) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.geocodeApi.getCityCoordinates(cityName, 1)
                if (response.isNotEmpty()) {
                    val location = response.first()
                    val displayName = cityName
                    val latitude = location.latitude
                    val longitude = location.longitude

                    callback(displayName, latitude, longitude)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to fetch city details", e)
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? = null
}
