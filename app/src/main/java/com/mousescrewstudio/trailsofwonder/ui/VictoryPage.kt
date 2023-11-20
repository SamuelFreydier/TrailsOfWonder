package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun VictoryPage(
    retourMenu: () -> Unit,
    huntId: String
) {
    // huntID contient l'ID de la chasse dans "Ongoing"
    // le but est ainsi de récup l'id de la Hunt, et le timestamp de départ

    var timeDate = Timestamp.now()
    val timestamp = Timestamp.now()

    LaunchedEffect(FirebaseAuth.getInstance().currentUser?.uid) {
        getHuntOngoing(
            huntId = huntId,
            onSuccess = { h ->
                timeDate = h
                println("Timestamp récupéré avec succès")
            }
        )
    }


    val compare = timestamp.compareTo(timeDate)
    val text = "Vous avez mis ${compare}s à la finir"


    Column(
        modifier = Modifier
            .padding(13.dp)
    ) {
        Text(text = "Bravo, la chasse est fini !",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary)

        //Spacer(modifier = Modifier.height(16.dp))
        //Text(text = huntId)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text)
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { retourMenu() },
            modifier = Modifier
                .fillMaxWidth())
        {
            Text(text = "Retour Menu")
        }
    }
}

fun getHuntOngoing(huntId: String, onSuccess: (Timestamp) -> Unit) {
    FirebaseFirestore.getInstance().collection("huntOnGoing")
        .document(huntId)
        .get()
        .addOnSuccessListener {
            val data = it.data
            if (data != null) {
                for((key, value) in data) {
                    if(key == "StartDate") {
                        //timeDate = value as Timestamp
                        onSuccess(value as Timestamp)
                    }
                }
            }
            else println("nope")
        }
        .addOnFailureListener {
            println("nono")
        }
}
