package com.mousescrewstudio.trailsofwonder.ui

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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@Composable
fun TeamCreation(
    navController: NavController
) {

    var teamName by remember { mutableStateOf("") }
    var teamMember by remember { mutableStateOf("") }
    var teamList by remember { mutableStateOf(listOf<String>()) }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text(
            text = "Créer une équipe",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(18.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nom de l'équipe...") },
            value = teamName,
            onValueChange = { teamName = it }
        )
        Spacer(modifier = Modifier.height(15.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nom d'un membre") },
            value = teamMember,
            onValueChange = { teamMember = it }
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
                    .height(80.dp)) {
                Text(text = "Ajouter un membre")
            }

            Spacer(Modifier.weight(0.2f))

            Button(
                onClick = {
                    val data = hashMapOf(
                        "TeamName" to teamName,
                        "Members" to teamList,
                    )
                    val db = FirebaseFirestore.getInstance().collection("huntOnGoing")
                    db.add(data)
                        .addOnSuccessListener {
                            println("Equipe ajouté avec succés")
                            navController.navigate("HuntOngoing/${it.id}")
                        }
                        .addOnFailureListener {
                            println("Erreur lors de l'ajout de l'équipe")
                        }

                },
                modifier = Modifier
                    .weight(1.5f)
                    .height(80.dp)) {
                Text(text = "Valider")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        teamList.forEach {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .border(1.dp, Color.LightGray, CutCornerShape(1.dp))
            ) {
                Text(
                    text = it,
                    modifier = Modifier
                        .padding(10.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewCreation () {
}