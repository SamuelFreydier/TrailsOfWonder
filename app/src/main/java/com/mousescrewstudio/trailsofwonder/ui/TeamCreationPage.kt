package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.mousescrewstudio.trailsofwonder.ui.database.createOngoingHunt

// Page de création d'équipe lors de la participation à une chasse
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamCreationPage(
    huntId: String,
    onStartClick: (String) -> Unit,
    onBackClick: () -> Unit
) {

    var teamName by remember { mutableStateOf("") }
    var teamMember by remember { mutableStateOf("") }
    var teamList by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Créer une équipe",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
            )
        }
    ) {innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Nom de l'équipe...") }
                )
                Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = teamMember,
                    onValueChange = { teamMember = it },
                    label = { Text("Nom d'un membre") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            teamList = teamList + teamMember
                            teamMember = ""
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(text = "Ajouter un membre")
                    }

                    Spacer(Modifier.weight(0.2f))

                    Button(
                        onClick = {
                            val data = hashMapOf(
                                "TeamName" to teamName,
                                "Members" to teamList,
                            )
                            println("ButtonClicked")
                            createOngoingHunt(
                                huntId = huntId,
                                teamMembers = teamList,
                                { ongoingHuntId ->
                                    println("startClick")

                                    onStartClick(ongoingHuntId)
                                },
                                { exception ->
                                    println("Erreur lors de la création de la chasse : $exception")
                                }
                            )
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = "Valider")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            items(teamList) {member ->
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = member,
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                    )
                    IconButton(
                        onClick = {
                            teamList = teamList - member
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    }
                }
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

        }
    }
}