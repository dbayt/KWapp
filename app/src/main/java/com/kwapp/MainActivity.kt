package com.kwapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.kwapp.ui.theme.WeatherAppTheme
import com.kwapp.viewmodel.LocationViewModel
import android.Manifest;
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kwapp.ui.WeatherScreen
import com.kwapp.viewmodel.LocationPermissionStatus


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        val locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        // Check if permission is already granted
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted) {
            Log.d("MainActivity", "Permission already granted, navigating to WeatherScreen")
            locationViewModel.onLocationPermissionGranted() // Update ViewModel
        }

        // Location Permission Launcher
        val locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d("MainActivity", "Location permission granted")
                locationViewModel.onLocationPermissionGranted()
            } else {
                Log.d("MainActivity", "Location permission denied")
                locationViewModel.onLocationPermissionDenied()
            }
        }

        // Set up the Compose UI
        setContent {
            WeatherAppTheme {
                val permissionStatus by locationViewModel.permissionStatus.collectAsState()

                when (permissionStatus) {
                    LocationPermissionStatus.GRANTED -> {
                        Log.d("MainActivity", "Navigating to WeatherScreen")
                        WeatherScreen()
                    }
                    LocationPermissionStatus.DENIED, LocationPermissionStatus.UNKNOWN -> {
                        Log.d("MainActivity", "Showing LocationPermissionScreen")
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
            Log.d("MainActivity", "Requesting location permission")
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}