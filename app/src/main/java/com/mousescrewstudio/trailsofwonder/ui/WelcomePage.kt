package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun WelcomePage(
    onNavigateToHuntCreation: () -> Unit,
    onNavigateToHuntJoin: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
// A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = onNavigateToHuntJoin) {
                Text(text = "Lancer une chasse")
            }
            Button(onClick = onNavigateToHuntCreation) {
                Text(text = "Créer une chasse")
            }
            Button(onClick = onNavigateToProfile) {
                Text(text = "Profil")
            }
        }
    }
}