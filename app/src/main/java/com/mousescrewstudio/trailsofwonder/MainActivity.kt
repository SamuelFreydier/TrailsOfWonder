package com.mousescrewstudio.trailsofwonder

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.mousescrewstudio.trailsofwonder.ui.theme.TrailsOfWonderTheme

class MainActivity : ComponentActivity() {
    // Stockage des variables utiles à la localisation
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null
    private lateinit var locationRequest: LocationRequest
    var locationRequired = false

    // Lancement de l'application
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            TrailsOfWonderTheme {
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser

                // Non connecté => Login / Connecté => Page d'accueil
                val startRoute = if(currentUser == null) Screen.Login else Screen.HuntJoin
                TrailsOfWonderApp(startDestination = startRoute.route)
            }
        }
    }

    // Utilisateur sur l'application => Mise à jour de la localisation
    override fun onResume() {
        super.onResume()
        if( locationRequired ) {
            startLocationUpdates()
        }
    }

    // Application en pause => Arrêt des mises à jour de localisation
    override fun onPause() {
        super.onPause()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }

    // Construction de la requête de localisation qui sera appelée en boucle selon certains critères (distance et temps minimum)
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

