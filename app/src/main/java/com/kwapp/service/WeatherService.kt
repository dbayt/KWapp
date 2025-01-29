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
import com.kwapp.retrofit.pojo.WeatherResponse
import com.kwapp.utils.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherService : Service() {


    companion object {
        private val _currentLocationFlow = MutableStateFlow<Location?>(null) // ✅ Add Location StateFlow
        val currentLocationFlow = _currentLocationFlow.asStateFlow() // ✅ Expose Location Flow

        private val _weatherFlow = MutableStateFlow<WeatherResponse?>(null)
        val weatherLiveData = _weatherFlow.asStateFlow() // ✅ Expose as StateFlow
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastSavedLocation: Location? = null // Store the last saved location


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Created")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d(TAG, "On location result")

                super.onLocationResult(locationResult)
                val lastLocation = locationResult.lastLocation

                if (lastLocation != null) {
                    val previousLocation = lastSavedLocation

                    // ✅ Check if the latitude or longitude difference is greater than 0.001
                    if (previousLocation == null ||
                        kotlin.math.abs(lastLocation.latitude - previousLocation.latitude) > 0.001 ||
                        kotlin.math.abs(lastLocation.longitude - previousLocation.longitude) > 0.001) {

                        Log.i(TAG, "New significant location update: Lat=${lastLocation.latitude}, Lon=${lastLocation.longitude}")

                        _currentLocationFlow.value = lastLocation // ✅ Update location flow
                        lastSavedLocation = lastLocation // ✅ Save this location as the last one
                        fetchWeatherData(lastLocation.latitude, lastLocation.longitude)

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
        // Get immediate location
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener { location ->
            if (location != null) {
                Log.i(TAG, "Immediate location: Lat=${location.latitude}, Lon=${location.longitude}")
                _currentLocationFlow.value = location // ✅ Update immediate location
                fetchWeatherData(location.latitude, location.longitude)
            } else {
                Log.w(TAG, "Immediate location is null")
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to get immediate location", e)
        }

        // Start continuous location updates
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

    private fun fetchWeatherData(lat: Double, lon: Double) {
        Log.d(TAG, "🌍 Fetching weather data for Lat: $lat, Lon: $lon")

        val call = RetrofitClient.weatherApi.getWeather(lat, lon)
        Log.d(TAG, "🌍 Sending Weather API request: $call")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                Log.d(TAG, "✅ Weather API Response received")

                if (response.isSuccessful) {
                    Log.d(TAG, "🌤️ Success: ${response.body()}")
                    _weatherFlow.value = response.body()
                } else {
                    Log.e(TAG, "❌ API Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e(TAG, "🚨 API Call Failed", t)
            }
        })
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
