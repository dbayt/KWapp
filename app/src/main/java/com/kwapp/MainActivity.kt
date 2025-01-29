package com.kwapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.kwapp.ui.theme.WeatherAppTheme
import com.kwapp.viewmodel.LocationViewModel
import android.Manifest;
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kwapp.service.WeatherService
import com.kwapp.ui.WeatherScreen
import com.kwapp.utils.TAG
import com.kwapp.viewmodel.LocationPermissionStatus


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        // Check if permission is granted
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted) {
            Log.d(TAG, "Permission already granted, starting LocationService")
            locationViewModel.onLocationPermissionGranted()
            startLocationService() // Start the location service automatically
        }

        // Location Permission Launcher
        val locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "Location permission granted")
                locationViewModel.onLocationPermissionGranted()
                startLocationService() // Start the location service after permission is granted
            } else {
                Log.d(TAG, "Location permission denied")
                locationViewModel.onLocationPermissionDenied()
            }
        }

        setContent {
            WeatherAppTheme {
                val permissionStatus by locationViewModel.permissionStatus.collectAsState()

                when (permissionStatus) {
                    LocationPermissionStatus.GRANTED -> {
                        Log.d(TAG, "Navigating to WeatherScreen")
                        WeatherScreen()
                    }
                    LocationPermissionStatus.DENIED, LocationPermissionStatus.UNKNOWN -> {
                        Log.d(TAG, "Showing LocationPermissionScreen")
                        LocationPermissionScreen(
                            requestLocationPermission = {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            viewModel = locationViewModel
                        )
                    }
                }
            }
        }

        // Ask for permission only if not already granted
        if (!isPermissionGranted) {
            Log.d(TAG, "Requesting location permission")
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startLocationService() {
        Log.d(TAG, "Starting LocationService")
        val serviceIntent = Intent(this, WeatherService::class.java)
        startService(serviceIntent)
    }
}