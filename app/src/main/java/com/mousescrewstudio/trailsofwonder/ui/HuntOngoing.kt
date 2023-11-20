package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HuntOngoing(
    ID : String
) {
    Text(text = ID)
    val db = FirebaseFirestore.getInstance()
    db.collection("huntOnGoing").document(ID).get()
        .addOnSuccessListener {
            println("Victoire ${it.data}")
        }
        .addOnFailureListener {
            println("Echec ${it.cause}")
        }
}