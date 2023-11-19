package com.mousescrewstudio.trailsofwonder.ui.database

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.parcelize.Parcelize

@Parcelize
data class Indice(
    var id: String = "",
    var huntId: String = "",
    var name: String = "",
    var description: String = "",
    var password: String? = "",
    var latitude: Float = 0f,
    var longitude: Float = 0f
) : Parcelable


fun saveIndice(indice: Indice) {
    val user = FirebaseAuth.getInstance().currentUser

    if(user != null) {
        val userId = user.uid
        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(indice.huntId)
            .collection("indices")
            .add(indice)
            .addOnSuccessListener { documentReference ->
                // Indice ajouté avec succès
                // documentReference.id contient l'id du nouvel indice
                println("Indice ajouté avec succès : ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                // Gestion d'erreur
                println("Erreur lors de la sauvegarde de l'indice : $exception")
            }
    }
}

fun getIndicesFromHunt(huntId: String, onSuccess: (List<Indice>) -> Unit, onFailure: (Exception) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid

        // Récupère toutes les chasses de l'utilisateur
        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(huntId)
            .collection("indices")
            .get()
            .addOnSuccessListener { result ->
                val indices = result.toObjects(Indice::class.java)
                val updatedIndices = indices.map {
                    it.copy(id = result.documents[indices.indexOf(it)].id)
                }
                onSuccess(updatedIndices)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

// Ajoutez cette fonction à votre repository (ou là où vous gérez les accès à Firestore)
fun deleteIndice(huntId: String, indiceId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid
        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(huntId)
            .collection("indices")
            .document(indiceId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}