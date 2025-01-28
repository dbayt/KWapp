package com.kwapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kwapp.viewmodel.LocationPermissionStatus
import com.kwapp.viewmodel.LocationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Mock ViewModel for Preview
class FakeLocationViewModel : LocationViewModel() {
    override val permissionStatus: StateFlow<LocationPermissionStatus> =
        MutableStateFlow(LocationPermissionStatus.UNKNOWN) // Mock initial value
}

// Main Composable Function

@Preview(showBackground = true, name = "Main Screen Preview")
@Composable
fun LocationPermissionScreenPreview() {
    // Provide mock/default values using FakeLocationViewModel
    LocationPermissionScreen(
        requestLocationPermission = {}, // No-op lambda
        viewModel = FakeLocationViewModel() // Pass the mock ViewModel
    )
}
