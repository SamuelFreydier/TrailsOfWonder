package com.mousescrewstudio.trailsofwonder.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mousescrewstudio.trailsofwonder.ui.database.Hunt
import com.mousescrewstudio.trailsofwonder.ui.database.Indice
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.mousescrewstudio.trailsofwonder.MainActivity
import com.mousescrewstudio.trailsofwonder.ui.database.IndiceWithValidation
import com.mousescrewstudio.trailsofwonder.ui.database.checkAndUnlockIndice
import com.mousescrewstudio.trailsofwonder.ui.database.getIndicesFromOngoingHunt
import com.mousescrewstudio.trailsofwonder.ui.database.getOngoingHunt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OngoingHuntPage(
    huntId: String,
    onBackClick: () -> Unit
) {
    var indices by remember { mutableStateOf<List<IndiceWithValidation>>(emptyList()) }
    val context = LocalContext.current as MainActivity
    var userLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    context.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    context.locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for( lo in p0.locations ) {
                userLocation = LatLng(lo.latitude, lo.longitude)
                println(userLocation)
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

    LaunchedEffect(huntId) {
        // Récupérer la liste des indices de la chasse en cours depuis Firebase
        getIndicesFromOngoingHunt(
            ongoingHuntId = huntId,
            onSuccess = { fetchedIndices ->
                indices = fetchedIndices
            },
            onFailure = { exception ->
                // Gestion des erreurs lors de la récupération des indices
                println("Erreur lors de la récupération des indices : $exception")
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chasse en cours",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Afficher la liste des indices
            OngoingHuntIndicesList(indices = indices)

            // Bouton pour valider la position
            Button(
                onClick = {
                          checkAndUnlockIndice(
                              huntId = huntId,
                              currentPos = userLocation,
                              { validated ->
                                  if(validated) {
                                      getIndicesFromOngoingHunt(
                                          ongoingHuntId = huntId,
                                          onSuccess = { fetchedIndices ->
                                              indices = fetchedIndices
                                          },
                                          onFailure = { exception ->
                                              // Gestion des erreurs lors de la récupération des indices
                                              println("Erreur lors de la récupération des indices : $exception")
                                          }
                                      )
                                  } else {
                                      Toast.makeText(context, "L'indice ne se trouve pas ici !", Toast.LENGTH_SHORT)
                                  }
                              }, { exception ->
                                  println("Erreur lors de la vérification de la position : $exception")
                              }
                          )
                    // TODO: Ajouter la logique pour valider la position et obtenir le prochain indice
                    // Ceci dépend de la logique spécifique du jeu
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Valider la position")
            }
        }
    }
}

@Composable
fun OngoingHuntIndicesList(indices: List<IndiceWithValidation>) {
    LazyColumn {
        items(indices) { indice ->
            OngoingHuntIndiceCard(indice = indice)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OngoingHuntIndiceCard(indice: IndiceWithValidation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = {
            // TODO: Ajouter la logique pour afficher les détails de l'indice (peut-être un fond de carte détaillé)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = indice.indice.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                text = indice.indice.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
