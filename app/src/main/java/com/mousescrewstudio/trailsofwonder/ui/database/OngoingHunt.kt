package com.mousescrewstudio.trailsofwonder.ui.database

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

// Classe représentant une chasse en cours (participation)
@Parcelize
data class OngoingHunt(
    var id: String = "",
    var huntId: String = "",
    var huntName: String = "",
    var teamMembers: List<String> = emptyList(),
    var indices: List<IndiceWithValidation> = emptyList(),
    var currentIndiceOrder: Int = 1,
    var launcherId: String = "",
    var startDate: Timestamp = Timestamp.now()
) : Parcelable

// Classe représentant un indice et s'il a été validé ou non
@Parcelize
data class IndiceWithValidation(
    var indice: Indice = Indice(),
    var isValidated: Boolean = false
) : Parcelable


// Classe représentant une chasse et la liste de ses indices (et s'ils ont été validés ou non)
@Parcelize
data class HuntWithIndices(
    var hunt: Hunt = Hunt(),
    var indices: List<IndiceWithValidation> = emptyList()
) : Parcelable

// Récupérer la chasse huntId en cours de joueur actif
fun getOngoingHunt(
    huntId: String,
    onSuccess: (OngoingHunt) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    println("getOngoingHunt")
    println(huntId)

    if (user != null) {
        db.collection("hunts")
            .document(user.uid)
            .collection("ongoingHunts")
            .document(huntId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val ongoingHunt = documentSnapshot.toObject(OngoingHunt::class.java)
                    if (ongoingHunt != null) {
                        onSuccess(ongoingHunt)
                    }
                    else
                        println("La chasse est null")
                } else
                    println("Pas de chasse avec cet ID")
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

// Récupère les détails d'une chasse (y compris les indices)
fun getHuntDetails(
    huntId: String,
    onSuccess: (HuntWithIndices) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        db.collection("publishedHunts")
            .document(huntId)
            .get()
            .addOnSuccessListener { result ->
                val hunt = result.toObject(Hunt::class.java)
                val userId = hunt?.creatorUserId
                if (hunt != null && userId != null) {
                    getIndicesFromHuntFromUser(
                        userId = userId,
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

// Fait participer l'utilisateur à une chasse
fun createOngoingHunt(
    huntId: String,
    teamMembers: List<String>,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
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
                    huntName = huntWithIndices.hunt.huntName,
                    teamMembers = teamMembers,
                    indices = huntWithIndices.indices,
                    launcherId = huntWithIndices.hunt.creatorUsername,
                    //user.displayName.toString(),
                    startDate = Timestamp.now()
                )

                println(huntWithIndices.indices)

                db.collection("hunts")
                    .document(user.uid)
                    .collection("ongoingHunts")
                    .add(ongoingHunt)
                    .addOnSuccessListener { docHuntRef ->
                        println("ongoinghuntadded")
                        val batch = db.batch()
                        huntWithIndices.indices.forEach { doc ->
                            println("foreach loop ${doc.indice}")
                            val docRef = db.collection("hunts")
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

// Vérifie si l'utilisateur est au bon endroit vis à vis du prochain indice de sa chasse, et si oui, débloque l'indice
fun checkAndUnlockIndice(huntId: String, currentPos: LatLng, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    if(user != null) {
        db.collection("hunts")
            .document(user.uid)
            .collection("ongoingHunts")
            .document(huntId)
            .get()
            .addOnSuccessListener {  result ->
                val ongoingHunt = result.toObject(OngoingHunt::class.java)
                val currentIndiceOrder = ongoingHunt?.currentIndiceOrder
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

// Récupère le prochain indice à débloquer d'une chasse en cours
fun getIndiceFromOrderFromOngoingHunt(
    ongoingHuntId: String,
    indiceOrder: Int,
    onSuccess: (IndiceWithValidation, String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
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
                    val indice = indices.first()
                    onSuccess(indice, result.documents[indices.indexOf(indice)].id)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

// Récupère les indices débloqués d'une chasse en cours
fun getIndicesFromOngoingHunt(
    ongoingHuntId: String,
    onSuccess: (List<IndiceWithValidation>, Boolean) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
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
                val allIndicesFound = indices.size == updatedIndices.size
                onSuccess(updatedIndices, allIndicesFound)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

// Récupération des chasses non terminées auxquelles l'utilisateur participe
fun getUserOngoingHunts(onSuccess: (List<OngoingHunt>) -> Unit, onFailure: (Exception) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid

        // Récupère toutes les chasses de l'utilisateur
        db.collection("hunts")
            .document(userId)
            .collection("ongoingHunts")
            .orderBy("huntName")
            .get()
            .addOnSuccessListener { result ->
                val hunts = result.toObjects(OngoingHunt::class.java)
                val notFinishedHunts = hunts.filter {ongoingHunt ->
                    ongoingHunt.currentIndiceOrder < ongoingHunt.indices.size && ongoingHunt.indices.isNotEmpty()
                }
                val updatedHunts = notFinishedHunts.map {
                    it.copy(id = result.documents[hunts.indexOf(it)].id)
                }
                onSuccess(updatedHunts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}