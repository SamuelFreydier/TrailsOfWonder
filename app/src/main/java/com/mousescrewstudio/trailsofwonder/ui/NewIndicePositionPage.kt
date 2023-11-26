package com.mousescrewstudio.trailsofwonder.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mousescrewstudio.trailsofwonder.MainActivity

// Page de positionnement d'un nouvel indice
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NewIndicePositionPage(
    huntId: String,
    onPositionValidated: (Float, Float) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current as MainActivity
    var userLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val zoomAmount = 18f
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, zoomAmount)
    }
    context.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    context.locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for( lo in p0.locations ) {
                userLocation = LatLng(lo.latitude, lo.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation, zoomAmount)
            }
        }
    }

    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            context.locationRequired = true
            context.startLocationUpdates()
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Si autorisé => Récupération de la localisation / Sinon => Demande de permission
    if (permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }) {
        // Get the location
        context.startLocationUpdates()
    } else {
        LaunchedEffect(permissionState) {
            launcherMultiplePermissions.launch(permissions)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Position du nouvel indice")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
    ) { innerPadding ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = userLocation),
                title = "Position actuelle",
                snippet = "Marqueur à la position actuelle"
            )
        }

        // Bottom section with controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Button to validate the selected position
            Button(
                onClick = {
                    // Use the current user location for validation
                    onPositionValidated(userLocation.latitude.toFloat(), userLocation.longitude.toFloat())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = "Valider la position")
            }
        }

    }


}