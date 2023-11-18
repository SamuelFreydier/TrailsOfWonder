package com.mousescrewstudio.trailsofwonder.ui.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

val db = FirebaseFirestore.getInstance()

data class Hunt(
    val huntName: String,
    val location: String,
    val difficulty: Int,
    val durationHours: Int,
    val durationMinutes: Int,
    val tags: List<String>
)

fun saveHunt(hunt: Hunt) {
    val user = FirebaseAuth.getInstance().currentUser

    if(user != null) {
        val userId = user.uid

        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .add(hunt)
            .addOnSuccessListener { documentReference ->
                // Chasse ajoutée avec succès
                // documentReference.id contient l'id du nouveau document
            }
            .addOnFailureListener { e ->
                // Gestion d'erreur
            }
    }
}

fun getUserHunts(onSuccess: (List<Hunt>) -> Unit, onFailure: (Exception) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid

        // Récupère toutes les chasses de l'utilisateur
        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .get()
            .addOnSuccessListener { result ->
                val hunts = mutableListOf<Hunt>()
                for (document in result) {
                    // Convertis chaque document en instance de la classe Hunt
                    val hunt = document.toObject(Hunt::class.java)
                    hunts.add(hunt)
                }
                onSuccess(hunts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}