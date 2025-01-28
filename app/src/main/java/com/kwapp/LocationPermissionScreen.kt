package com.kwapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kwapp.viewmodel.LocationPermissionStatus
import com.kwapp.viewmodel.LocationViewModel

@Composable
fun LocationPermissionScreen(
    requestLocationPermission: () -> Unit,
    viewModel: LocationViewModel
) {
    // Observing the ViewModel's state
    val permissionStatus by viewModel.permissionStatus.collectAsState()

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (permissionStatus) {
                LocationPermissionStatus.GRANTED -> {
                    Text(text = "Permission Granted! You can now access location.")
                }
                LocationPermissionStatus.DENIED -> {
                    Text(text = "Permission Denied! Please grant permission to proceed.")
                }
                LocationPermissionStatus.UNKNOWN -> {
                    Text(text = "Location permission is required to proceed.")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = requestLocationPermission) {
                Text("Request Location Permission")
            }
        }
    }
}
