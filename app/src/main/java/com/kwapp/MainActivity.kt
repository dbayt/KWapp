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

        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted) {
            Log.d(TAG, "✅ Permission already granted, starting WeatherService & navigating to WeatherScreen")
            locationViewModel.onLocationPermissionGranted()
            startWeatherService() // ✅ Start the service here
        }

        val locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "✅ Location permission granted")
                locationViewModel.onLocationPermissionGranted()
                startWeatherService() // ✅ Start service when permission is granted
            } else {
                Log.d(TAG, "🚨 Location permission denied")
                locationViewModel.onLocationPermissionDenied()
            }
        }

        setContent {
            WeatherAppTheme {
                val permissionStatus by locationViewModel.permissionStatus.collectAsState()

                when (permissionStatus) {
                    LocationPermissionStatus.GRANTED -> {
                        Log.d(TAG, "📌 Navigating to WeatherScreen")
                        WeatherScreen(lifecycleOwner = this@MainActivity)
                    }
                    LocationPermissionStatus.DENIED, LocationPermissionStatus.UNKNOWN -> {
                        Log.d(TAG, "🔒 Showing LocationPermissionScreen")
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

        if (!isPermissionGranted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // ✅ Function to Start WeatherService
    private fun startWeatherService() {
        Log.d(TAG, "🌍 Starting WeatherService...")
        val serviceIntent = Intent(this, WeatherService::class.java)
        startService(serviceIntent) // ✅ This will start the service
    }
}