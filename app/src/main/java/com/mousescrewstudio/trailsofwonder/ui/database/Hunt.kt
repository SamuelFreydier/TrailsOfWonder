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
    var tags: List<String> = emptyList(),
    var comment: List<String> = emptyList(),
    var id: String = ""
) {
    //constructor(): this("", "", 0, 0, 0, emptyList())
}

fun saveHunt(hunt: Hunt, onSuccess: (String) -> Unit) {
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
                val huntId = documentReference.id
                onSuccess(huntId)
                println("DocumentReference : ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                // Gestion d'erreur
                println("Erreur lors de la sauvegarde : $exception")
            }
    }
}

fun updateHunt(huntId: String, hunt: Hunt, onSuccess: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if(user != null) {
        val userId = user.uid

        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(huntId)
            .update("huntName", hunt.huntName, "location", hunt.location, "difficulty", hunt.difficulty, "durationHours", hunt.durationHours, "durationMinutes", hunt.durationMinutes)
            .addOnSuccessListener {
                // Chasse mise à jour avec succès
                onSuccess(huntId)
            }
            .addOnFailureListener { exception ->
                // Gestion d'erreur
                println("Erreur lors de la mise à jour : $exception")
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
                val updatedHunts = hunts.map {
                    it.copy(id = result.documents[hunts.indexOf(it)].id)
                }
                onSuccess(updatedHunts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

// Récupère une chasse à partir de son ID
fun getHuntFromId(huntId: String, onSuccess: (Hunt) -> Unit, onFailure: (Exception) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    println("getHuntFromId")
    if (user != null) {
        val userId = user.uid
        println("userNotNull getHuntFromId")
        println(huntId)

        // Récupère toutes les chasses de l'utilisateur
        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(huntId)
            .get()
            .addOnSuccessListener { result ->
                println("success")
                val hunt = result.toObject(Hunt::class.java)
                val updatedHunt = hunt?.copy(id = result.id)
                if (updatedHunt != null) {
                    println("updatedhunt not null")
                    onSuccess(updatedHunt)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}


fun getAllUser() : MutableList<String> {
    val firestore = FirebaseFirestore.getInstance()
    val fireCollection = firestore.collection("username")
    val fireDocument = fireCollection.document("UsernameList")

    val list = mutableListOf<String>()

    fireDocument
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val data = document.data
                if (data != null) {
                    for (value in data) {
                        val x = value.toString()
                        list.add(x)
                    }
                }
            }

            //println("ListUID $list")
            //println("List size: ${list.size}")
        }

    //println("ListUID $list")
    //println("List size: ${list.size}")

    return list
}
