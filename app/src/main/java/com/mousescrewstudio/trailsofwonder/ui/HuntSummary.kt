package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.firestore.FirebaseFirestore

//data class Hunt(val id: String, val huntName: String, val location: String)
//chasse = Hunt("null", "null", -1, -1, -1, listOf<String>("-1"))
//summaryComposable()

@Composable
fun HuntSummary() {
    val resultat = 10

    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("usename").document("DocumentForAll")
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val value = document.getString("user01")
                println("Valeur lue depuis Firestore : $value")
            } else {
                println("Aucun document trouvé")
            }
        }
        .addOnFailureListener { exception ->
            // Gérer les erreurs ici
            println("Erreur lors de la lecture des données : $exception")
        }



    /*val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val userId = user.uid

        resultat = "ici"

        // Récupère toutes les chasses de l'utilisateur
        com.mousescrewstudio.trailsofwonder.ui.database.db.collection("hunts")
            .document(userId)
            .collection("userHunts")
            .get()
            .addOnSuccessListener { result ->
                resultat = "caca"
            }
            .addOnFailureListener { exception ->
                resultat = "pedro"
            }
    }*/





    Text(text = resultat.toString())
}
/*
fun NavGraphBuilder.summaryComposable() {
    composable(
        route = "summary/{x}",
        arguments = listOf(navArgument("x") { type = NavType.IntType })
    ) { backStackEntry ->
        val x = backStackEntry.arguments?.getInt("x") ?: 0
        HuntSummary(navController = rememberNavController())
    }
}
*/