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
import com.kwapp.utils.TAG

class WeatherService : Service() {


    companion object {
        val currentLocationLiveData = MutableLiveData<Location?>()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Created")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult.lastLocation
                if (lastLocation != null) {
                    Log.i(TAG, "Latitude: ${lastLocation.latitude}, Longitude: ${lastLocation.longitude}")
                    currentLocationLiveData.postValue(lastLocation)
                } else {
                    Log.w(TAG, "Received null location")
                }
            }
        }

        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        // Request immediate single location update
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener { location ->
            if (location != null) {
                Log.i(TAG, "On service start mmediate location: Lat=${location.latitude}, Lon=${location.longitude}")
                currentLocationLiveData.postValue(location)
            } else {
                Log.w(TAG, "Immediate location is null")
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to get immediate location", e)
        }

        // Continuous location updates with lower priority after the first location
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


    override fun onBind(intent: Intent?): IBinder? = null
}
