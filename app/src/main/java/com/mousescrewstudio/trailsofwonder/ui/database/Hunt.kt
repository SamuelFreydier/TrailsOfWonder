package com.mousescrewstudio.trailsofwonder.ui.database

import android.os.Parcelable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.parcelize.Parcelize

val db = FirebaseFirestore.getInstance()

// Liste des tags prédéfinis disponibles (création ET recherche)
val predefinedTags = listOf("Forêt", "Ville", "Montagne", "Randonnée")

// Classe représentant une chasse au trésor créée/publiée
@Parcelize
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
    var creatorUsername: String = "",
    var isPublic: Boolean = false
) : Parcelable

// Création d'une chasse au trésor
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
        .whereEqualTo("public", true)
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
            var hunt = result.toObject(Hunt::class.java)
            if (hunt != null) {
                hunt.id = result.id
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

                getIndicesFromHunt(hunt.id, { indices ->
                    var batch = db.batch()

                    // Nouvelle publication
                    db.collection("publishedHunts")
                        .add(hunt)
                        .addOnSuccessListener { docHuntRef ->
                            indices.forEach { doc ->
                                var docRef = db.collection("publishedHunts")
                                    .document(docHuntRef.id)
                                    .collection("indices")
                                    .document()
                                batch.set(docRef, doc)
                            }
                            batch.commit()
                                .addOnSuccessListener {
                                    onSuccess()
                                }
                                .addOnFailureListener { exception ->
                                    onFailure(exception)
                                }
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }, {exception ->
                    onFailure(exception)
                })


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

// Mise à jour d'une chasse
fun updateHunt(huntId: String, hunt: Hunt, onSuccess: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if(user != null) {
        val userId = user.uid

        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(huntId)
            .update("huntName", hunt.huntName, "location", hunt.location, "difficulty", hunt.difficulty, "durationHours", hunt.durationHours, "durationMinutes", hunt.durationMinutes, "tags", hunt.tags, "creatorUserId", userId, "creatorUsername", user.displayName.toString(), "public", hunt.isPublic)
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

// Récupération des chasses créées par l'utilisateur courant
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
