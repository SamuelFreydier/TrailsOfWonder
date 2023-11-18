package com.mousescrewstudio.trailsofwonder.ui.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

val db = FirebaseFirestore.getInstance()

data class Hunt (
    var huntName: String = "",
    var location: String = "",
    var difficulty: Int = 0,
    var durationHours: Int = 0,
    var durationMinutes: Int = 0,
    var tags: List<String> = emptyList()
) {
    //constructor(): this("", "", 0, 0, 0, emptyList())
}

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
                println("DocumentReference : ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                // Gestion d'erreur
                println("Erreur lors de la sauvegarde : $exception")
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
                val hunts = result.toObjects(Hunt::class.java)
                /*for (document in result) {
                    // Convertis chaque document en instance de la classe Hunt
                    //val hunt : Hunt = document.toObject(Hunt::class.java)
                    //hunts.add(hunt)
                }*/
                onSuccess(hunts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
/*
fun parseHuntDocument(document: DocumentSnapshot): Hunt? {
    return try {
        val json = Json { ignoreUnknownKeys = true }
        val hunt = json.decodeFromString(Hunt.serializer(), document.data?.toJson())
        hunt
    } catch (e: Exception) {
        // Gérez l'erreur de désérialisation ici
        null
    }
}*/