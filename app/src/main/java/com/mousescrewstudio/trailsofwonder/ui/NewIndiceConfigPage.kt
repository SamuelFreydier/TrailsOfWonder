package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.mousescrewstudio.trailsofwonder.ui.database.Indice
import com.mousescrewstudio.trailsofwonder.ui.database.saveIndice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewIndiceConfigPage(
    huntId: String,
    latitude: Float,
    longitude: Float,
    onIndiceConfigured: (Indice) -> Unit,
    onBackClick: () -> Unit
) {
    var indiceName by remember { mutableStateOf("") }
    var indiceDescription by remember { mutableStateOf("") }
    var indicePassword by remember { mutableStateOf("") }

    Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(text = "Configuration de l'indice") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Input fields for indice configuration
            OutlinedTextField(
                value = indiceName,
                onValueChange = { indiceName = it },
                label = { Text("Nom de l'indice") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            OutlinedTextField(
                value = indiceDescription,
                onValueChange = { indiceDescription = it },
                label = { Text("Description de l'indice") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            OutlinedTextField(
                value = indicePassword,
                onValueChange = { indicePassword = it },
                label = { Text("Mot de passe (facultatif)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Text(
                text = "Coordonnées : ${latitude}, ${longitude}",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Validate button
            Button(
                onClick = {
                    val newIndice =
                        Indice(
                            huntId = huntId,
                            name = indiceName,
                            description = indiceDescription,
                            password = if (indicePassword.isNotBlank()) indicePassword else null,
                            latitude = latitude,
                            longitude = longitude // Set coordinates accordingly
                        )

                    if (newIndice != null) {
                        saveIndice(newIndice)
                        onIndiceConfigured(newIndice)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = "Valider")
            }
        }

    }
    

}
