package com.mousescrewstudio.trailsofwonder.ui.database

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth

data class Indice(
    var huntId: String = "",
    var name: String = "",
    var description: String = "",
    var password: String? = "",
    var coordinates: LatLng = LatLng(0.0, 0.0)
)

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