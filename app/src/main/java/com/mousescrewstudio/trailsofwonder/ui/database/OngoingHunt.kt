package com.mousescrewstudio.trailsofwonder.ui.database

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

@Parcelize
data class OngoingHunt(
    var huntId: String = "",
    var teamMembers: List<String> = emptyList(),
    var indices: List<IndiceWithValidation> = emptyList(),
    var currentIndiceOrder: Int = 1
) : Parcelable

@Parcelize
data class IndiceWithValidation(
    var indice: Indice = Indice(),
    var isValidated: Boolean = false
) : Parcelable

@Parcelize
data class HuntWithIndices(
    var hunt: Hunt = Hunt(),
    var indices: List<IndiceWithValidation> = emptyList()
) : Parcelable

// Récupère les détails d'une chasse (y compris les indices)
fun getHuntDetails(
    huntId: String,
    onSuccess: (HuntWithIndices) -> Unit,
    onFailure: (Exception) -> Unit
) {
    var user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        db.collection("publishedHunts")
            .document(huntId)
            .get()
            .addOnSuccessListener { result ->
                val hunt = result.toObject(Hunt::class.java)
                if (hunt != null) {
                    getIndicesFromHunt(
                        huntId = hunt.id,
                        onSuccess = { indices ->
                            hunt.id = result.id
                            val huntWithIndices = HuntWithIndices(hunt = hunt, indices = indices.map { IndiceWithValidation(indice = it) })
                            onSuccess(huntWithIndices)
                        },
                        onFailure = { exception ->
                            onFailure(exception)
                        }
                    )
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

fun createOngoingHunt(
    huntId: String,
    teamMembers: List<String>,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    var user = FirebaseAuth.getInstance().currentUser
    println("createOngoing")
    println(huntId)

    if (user != null) {
        getHuntDetails(
            huntId = huntId,
            onSuccess = { huntWithIndices ->
                println("huntdetailsgotten")

                // Le premier indice est déjà validé, il permet de commencer la chasse avec les premières informations
                huntWithIndices.indices.first().isValidated = true

                val ongoingHunt = OngoingHunt(
                    huntId = huntWithIndices.hunt.id,
                    teamMembers = teamMembers,
                    indices = huntWithIndices.indices
                )

                println(huntWithIndices.indices)

                db.collection("hunts")
                    .document(user.uid)
                    .collection("ongoingHunts")
                    .add(ongoingHunt)
                    .addOnSuccessListener { docHuntRef ->
                        println("ongoinghuntadded")
                        var batch = db.batch()
                        huntWithIndices.indices.forEach { doc ->
                            println("foreach loop ${doc.indice}")
                            var docRef = db.collection("hunts")
                                .document(user.uid)
                                .collection("ongoingHunts")
                                .document(docHuntRef.id)
                                .collection("indices")
                                .document()
                            batch.set(docRef, doc)
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                println("success ${docHuntRef.id}")
                                onSuccess(docHuntRef.id)
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            },
            onFailure = { exception ->
                onFailure(exception)
            }
        )
    }
}

fun checkAndUnlockIndice(huntId: String, currentPos: LatLng, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
    var user = FirebaseAuth.getInstance().currentUser
    if(user != null) {
        db.collection("hunts")
            .document(user.uid)
            .collection("ongoingHunts")
            .document(huntId)
            .get()
            .addOnSuccessListener {  result ->
                var ongoingHunt = result.toObject(OngoingHunt::class.java)
                var currentIndiceOrder = ongoingHunt?.currentIndiceOrder
                if (currentIndiceOrder != null) {
                    getIndiceFromOrderFromOngoingHunt(
                        huntId,
                        currentIndiceOrder,
                        { indice, indiceId ->
                            if( abs(indice.indice.latitude - currentPos.latitude) < 0.0001 &&
                                abs(indice.indice.longitude - currentPos.longitude) < 0.0001 ) {
                                // Même position
                                db.collection("hunts")
                                    .document(user.uid)
                                    .collection("ongoingHunts")
                                    .document(huntId)
                                    .update("currentIndiceOrder", currentIndiceOrder + 1)
                                db.collection("hunts")
                                    .document(user.uid)
                                    .collection("ongoingHunts")
                                    .document(huntId)
                                    .collection("indices")
                                    .document(indiceId)
                                    .update("validated", true)
                                indice.isValidated = true
                                onSuccess(true)
                            } else {
                                onSuccess(false)
                            }
                        }, { exception -> onFailure(exception)
                        }
                    )
                }
            }
    }
}

// Récupère les indices actuels d'une ongoingHunt
fun getIndiceFromOrderFromOngoingHunt(
    ongoingHuntId: String,
    indiceOrder: Int,
    onSuccess: (IndiceWithValidation, String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    var user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        db.collection("hunts")
            .document(user.uid)
            .collection("ongoingHunts")
            .document(ongoingHuntId)
            .collection("indices")
            .whereEqualTo("indice.order", indiceOrder)
            .get()
            .addOnSuccessListener { result ->
                val indices = result.toObjects(IndiceWithValidation::class.java)
                if(!indices.isEmpty()) {
                    var indice = indices.first()
                    onSuccess(indice, result.documents[indices.indexOf(indice)].id)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

// Récupère les indices actuels d'une ongoingHunt
fun getIndicesFromOngoingHunt(
    ongoingHuntId: String,
    onSuccess: (List<IndiceWithValidation>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    var user = FirebaseAuth.getInstance().currentUser
    println("ongoingHunt $ongoingHuntId")
    if (user != null) {
        db.collection("hunts")
            .document(user.uid)
            .collection("ongoingHunts")
            .document(ongoingHuntId)
            .collection("indices")
            .get()
            .addOnSuccessListener { result ->
                val indices = result.toObjects(IndiceWithValidation::class.java)
                println("indices $indices")
                val updatedIndices = indices.filter { indice -> indice.isValidated }
                    .sortedBy { it.indice.order }
                println("updatedIndices $updatedIndices")
                onSuccess(updatedIndices)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}