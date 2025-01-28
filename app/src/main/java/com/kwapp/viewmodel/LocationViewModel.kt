package com.kwapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class LocationPermissionStatus {
    GRANTED, DENIED, UNKNOWN
}

open class LocationViewModel : ViewModel() {
    private val _permissionStatus = MutableStateFlow(LocationPermissionStatus.UNKNOWN)
    open val permissionStatus: StateFlow<LocationPermissionStatus> = _permissionStatus

    fun onLocationPermissionGranted() {
        _permissionStatus.value = LocationPermissionStatus.GRANTED
    }

    fun onLocationPermissionDenied() {
        _permissionStatus.value = LocationPermissionStatus.DENIED
    }
}
