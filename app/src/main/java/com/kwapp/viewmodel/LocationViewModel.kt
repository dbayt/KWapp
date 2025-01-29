package com.kwapp.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kwapp.service.WeatherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class LocationPermissionStatus {
    GRANTED, DENIED, UNKNOWN
}

open class LocationViewModel : ViewModel() {
    val currentLocation: StateFlow<Location?> = WeatherService.currentLocationFlow

    private val _permissionStatus = MutableStateFlow(LocationPermissionStatus.UNKNOWN)
    open val permissionStatus: StateFlow<LocationPermissionStatus> = _permissionStatus

    fun onLocationPermissionGranted() {
        _permissionStatus.value = LocationPermissionStatus.GRANTED
    }

    fun onLocationPermissionDenied() {
        _permissionStatus.value = LocationPermissionStatus.DENIED
    }

    fun onLocationPermissionUnknown() {
        _permissionStatus.value = LocationPermissionStatus.UNKNOWN
    }
}
