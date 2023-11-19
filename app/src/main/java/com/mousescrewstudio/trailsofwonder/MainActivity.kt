package com.mousescrewstudio.trailsofwonder

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.mousescrewstudio.trailsofwonder.ui.theme.TrailsOfWonderTheme

class MainActivity : ComponentActivity() {
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null
    private lateinit var locationRequest: LocationRequest
    var locationRequired = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //locationCallback = object : LocationCallback() {
        //    override fun onLocationResult(p0: LocationResult) {
        //        super.onLocationResult(p0)
        //        currentLocation = p0.lastLocation
//
        //    }
        //}

        // Initialisation de Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            TrailsOfWonderTheme {
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val startRoute = if(currentUser == null) Screen.Login else Screen.Welcome
                TrailsOfWonderApp(startDestination = startRoute.route)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        if( locationRequired ) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationCallback?.let {
            locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).apply {
                setMinUpdateDistanceMeters(5f)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

}

