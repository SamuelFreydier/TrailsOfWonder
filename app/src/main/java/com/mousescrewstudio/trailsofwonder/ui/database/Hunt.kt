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
    var id: String = "",
    var creatorUserId: String = "",
    var creatorUsername: String = ""
)

fun saveHunt(hunt: Hunt, onSuccess: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if(user != null) {
        val userId = user.uid
        hunt.creatorUserId = userId
        hunt.creatorUsername = user.displayName.toString()

        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .add(hunt)
            .addOnSuccessListener { documentReference ->
                // Chasse ajoutée avec succès
                // documentReference.id contient l'id du nouveau document
                val huntId = documentReference.id

                //db.collection("huntsList")
                //    .add(hunt).addOnSuccessListener {
                //        println("Ajout : ${it.id}")
                //    }
                //    .addOnFailureListener {
                //        println("Erreur lors de la sauvegarde : $it")
                //    }

                onSuccess(huntId)
                println("DocumentReference : ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                // Gestion d'erreur
                println("Erreur lors de la sauvegarde : $exception")
            }


    }
}

// Récupération des chasses publiées
fun getPublishedHunts(onSuccess: (List<Hunt>) -> Unit, onFailure: (Exception) -> Unit) {
    // Récupère toutes les chasses de l'utilisateur
    db.collection("publishedHunts")
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

// Récupère une chasse publiée avec son ID
fun getPublishedHuntById(huntId: String, onSuccess: (Hunt) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("publishedHunts")
        .document(huntId)
        .get()
        .addOnSuccessListener { result ->
            val hunt = result.toObject(Hunt::class.java)
            if (hunt != null) {
                onSuccess(hunt)
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

// Publication d'une chasse
fun publishHunt(hunt: Hunt, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    // Si la chasse a déjà été publiée, on écrase l'ancienne publication
    removePublishedHunt(
        huntId = hunt.id,
        onSuccess = {
            val user = FirebaseAuth.getInstance().currentUser

            if(user != null) {
                val userId = user.uid
                hunt.creatorUserId = userId
                hunt.creatorUsername = user.displayName.toString()
                // Nouvelle publication
                db.collection("publishedHunts")
                    .add(hunt)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }
        },
        onFailure = { exception ->
            onFailure(exception)
        }
    )
}

// Suppression d'une chasse publiée
fun removePublishedHunt(huntId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("publishedHunts")
        .whereEqualTo("id", huntId)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Il devrait y avoir au plus un document, mais parcourons les résultats au cas où
                for (document in querySnapshot.documents) {
                    // Supprimer le document correspondant à la chasse publiée
                    document.reference.delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            } else {
                // Aucun document correspondant à cet ID n'a été trouvé
                // Cela peut se produire si la chasse n'a pas encore été publiée
                onSuccess()
            }
        }
        .addOnFailureListener { exception ->
            // Gestion d'erreur lors de la récupération du document
            onFailure(exception)
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
            .update("huntName", hunt.huntName, "location", hunt.location, "difficulty", hunt.difficulty, "durationHours", hunt.durationHours, "durationMinutes", hunt.durationMinutes, "tags", hunt.tags, "creatorUserId", userId, "creatorUsername", user.displayName.toString())
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
            .orderBy("huntName")
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

// Supprime une chasse
fun deleteHunt(huntId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid
        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(huntId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

fun getOngoingHunt(huntID : String, onSuccess: (List<Hunt>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("publishedHunts")
        .whereEqualTo("huntName", huntID)
        .addSnapshotListener { snapshot, e ->
            if (e != null) {
                println("Erreur")
                return@addSnapshotListener
            }

            var listHunt = mutableListOf<Hunt>()
            if (snapshot != null && !snapshot.isEmpty) {
                val m = snapshot.toObjects(Hunt::class.java)
                listHunt = m
            }

            onSuccess(listHunt)
        }
}
