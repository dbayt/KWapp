package com.kwapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.kwapp.ui.theme.WeatherAppTheme
import com.kwapp.viewmodel.LocationViewModel
import android.Manifest;
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kwapp.ui.WeatherScreen
import com.kwapp.viewmodel.LocationPermissionStatus


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        val locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        // Location Permission Launcher
        val locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Permission granted
                Log.d("MainActivity", "Location permission granted")
                locationViewModel.onLocationPermissionGranted()
            } else {
                // Permission denied
                Log.d("MainActivity", "Location permission denied")
                locationViewModel.onLocationPermissionDenied()
            }
        }

        // Set up the Compose UI
        setContent {
            WeatherAppTheme {
                // Observe the location permission state from the ViewModel
                val permissionStatus by locationViewModel.permissionStatus.collectAsState()

                when (permissionStatus) {
                    LocationPermissionStatus.GRANTED -> {
                        Log.d("MainActivity", "Permission status: GRANTED")
                        WeatherScreen()
                    }
                    LocationPermissionStatus.DENIED -> {
                        Log.d("MainActivity", "Permission status: DENIED")
                        LocationPermissionScreen(
                            requestLocationPermission = {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            viewModel = locationViewModel
                        )
                    }
                    LocationPermissionStatus.UNKNOWN -> {
                        Log.d("MainActivity", "Permission status: UNKNOWN")
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
    }
}