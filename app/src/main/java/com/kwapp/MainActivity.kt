package com.kwapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.kwapp.ui.theme.WeatherAppTheme
import com.kwapp.viewmodel.LocationViewModel
import android.Manifest;


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
                locationViewModel.onLocationPermissionGranted()
            } else {
                locationViewModel.onLocationPermissionDenied()
            }
        }

        // Set up the Compose UI
        setContent {
            WeatherAppTheme {
                LocationPermissionScreen(
                    requestLocationPermission = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    viewModel = locationViewModel // Pass ViewModel to the Composable
                )
            }
        }
    }
}