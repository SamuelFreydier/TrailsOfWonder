package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mousescrewstudio.trailsofwonder.R

data class TreasureHunt(val title: String)

val dummyTreasureHunts = List(3) {
    TreasureHunt("Treasure Hunt $it")
}

@Composable
fun ProfilePage(
    username: String,
    onSettingsClick: () -> Unit,
    onEditTreasureHuntClick: (String) -> Unit
) {
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
            text = "Your Treasure Hunts",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Liste des chasses au trésor de l'utilisateur
        LazyColumn {
            items(dummyTreasureHunts) { treasureHunt ->
                TreasureHuntItem(
                    treasureHunt = treasureHunt,
                    onEditClick = { onEditTreasureHuntClick(it) }
                )
            }
        }
    }
}
@Composable
fun TreasureHuntItem(treasureHunt: TreasureHunt, onEditClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = treasureHunt.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onEditClick(treasureHunt.title) },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text("Edit Hunt")
        }
    }
}

