package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.mousescrewstudio.trailsofwonder.ui.database.Hunt
import com.mousescrewstudio.trailsofwonder.ui.database.getUserHunts

data class TreasureHunt(val title: String)

val dummyTreasureHunts = List(3) {
    TreasureHunt("Treasure Hunt $it")
}

// Page de profil
@Composable
fun ProfilePage(
    username: String,
    onSettingsClick: () -> Unit,
    onEditHuntClick: (String) -> Unit
) {
    var userHunts by remember { mutableStateOf(emptyList<Hunt>()) }

    LaunchedEffect(FirebaseAuth.getInstance().currentUser?.uid) {
        getUserHunts(
            onSuccess = { hunts ->
                userHunts = hunts
                println("Chasses récupérées avec succès")
            },
            onFailure = { exception ->
                // Erreur à gérer
                println("Erreur lors de la récupération des chasses : $exception")
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Profil de $username",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = { onSettingsClick() }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Vos chasses au trésor",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Liste des chasses au trésor de l'utilisateur
        LazyColumn {
            items(userHunts) { userHunt ->
                ProfileHuntItem(
                    hunt = userHunt,
                    onEditClick = { onEditHuntClick(it) }
                )
            }
        }
    }
}

// Représente une chasse créée par l'utilisateur
@Composable
fun ProfileHuntItem(hunt: Hunt, onEditClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = hunt.huntName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onEditClick(hunt.id) },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text("Éditer")
        }
    }
}

