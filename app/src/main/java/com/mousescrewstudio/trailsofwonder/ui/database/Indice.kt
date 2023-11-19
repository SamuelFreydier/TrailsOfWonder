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
    var longitude: Float = 0f,
    var order: Int = 0
) : Parcelable

// Crée un indice dans une chasse
fun saveIndice(indice: Indice, onSuccess: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid
        val huntRef = db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(indice.huntId)
            .collection("indices")

        // Récupérer le nombre d'indices actuels dans la chasse
        huntRef.get()
            .addOnSuccessListener { result ->
                val currentIndexCount = result.size()

                // Initialiser l'ordre du nouvel indice
                indice.order = currentIndexCount

                // Ajouter l'indice avec l'ordre correct
                huntRef.add(indice)
                    .addOnSuccessListener { documentReference ->
                        // Mise à jour de l'ordre après l'ajout réussi
                        //val newIndex = indice.copy(id = documentReference.id)
                        //updateIndiceOrder(newIndex, newIndex.order)
                        onSuccess()
                        println("Indice ajouté avec succès : ${documentReference.id}")
                    }
                    .addOnFailureListener { exception ->
                        // Gestion d'erreur
                        println("Erreur lors de la sauvegarde de l'indice : $exception")
                    }
            }
            .addOnFailureListener { exception ->
                // Gestion d'erreur lors de la récupération du nombre d'indices
                println("Erreur lors de la récupération du nombre d'indices : $exception")
            }
    }
}

// Met à jour l'ordre de l'indice par rapport aux autres indices de la chasse
fun updateIndiceOrder(indice: Indice, newOrder: Int) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid
        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(indice.huntId)
            .collection("indices")
            .document(indice.id)
            .update("order", newOrder)
            .addOnSuccessListener {
                // Succès de la mise à jour de l'ordre
                println("Ordre de l'indice mis à jour avec succès")
            }
            .addOnFailureListener { exception ->
                // Gestion d'erreur
                println("Erreur lors de la mise à jour de l'ordre de l'indice : $exception")
            }
    }
}

// Met à jour les informations d'un indice
fun updateIndice(indice: Indice, newName: String, newDescription: String, newPassword: String, onSuccess: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid
        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(indice.huntId)
            .collection("indices")
            .document(indice.id)
            .update("name", newName, "description", newDescription, "password", newPassword)
            .addOnSuccessListener {
                // Succès de la mise à jour de l'ordre
                onSuccess()
                println("Indice mis à jour avec succès")
            }
            .addOnFailureListener { exception ->
                // Gestion d'erreur
                println("Erreur lors de la mise à jour de l'indice : $exception")
            }
    }
}

// Récupère tous les indices d'une chasse
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
            .orderBy("order")
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

// Récupère un indice à partir de son ID et de la chasse
fun getIndiceFromId(huntId: String, indiceId: String, onSuccess: (Indice) -> Unit, onFailure: (Exception) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid

        // Récupère toutes les chasses de l'utilisateur
        db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .document(huntId)
            .collection("indices")
            .document(indiceId)
            .get()
            .addOnSuccessListener { result ->
                val indice = result.toObject(Indice::class.java)
                val updatedIndice = indice?.copy(id = result.id)
                if (updatedIndice != null) {
                    onSuccess(updatedIndice)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

// Supprime un indice
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