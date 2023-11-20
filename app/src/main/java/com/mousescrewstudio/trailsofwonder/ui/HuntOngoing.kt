package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mousescrewstudio.trailsofwonder.ui.database.Hunt
import com.mousescrewstudio.trailsofwonder.ui.database.getOngoingHunt

@Composable
fun HuntOngoing(
    ID : String, // ID la HuntOngoing, != de l'ID de la hunt (contenu dedans par contre)
    //onClickVictory: (String) -> Unit,
    navController: NavController
) {
    var listHunt by remember { mutableStateOf<List<Hunt>>(emptyList()) }
    var huntID = ""

    // Retrouve la hunt dans les HuntOnGoing
    LaunchedEffect(FirebaseAuth.getInstance().currentUser?.uid) {
        val db = FirebaseFirestore.getInstance()
        db.collection("huntOnGoing").document(ID).get()
            .addOnSuccessListener {
                    if (it != null) {
                        val data = it.data
                        if (data != null) {
                            println("Victoire ${it.data}")
                            for ((key, value) in data) {
                                if(key == "Hunt")
                                    huntID = value.toString()
                            }
                        }
                        println(huntID)
                    }
            }
            .addOnFailureListener {
                println("Echec ${it.cause}")
            }

        if(huntID != "") {
            getOngoingHunt(huntID) { loadedMessages ->
                listHunt = loadedMessages
            }
        }
    }


    Column {
        Text(text = ID)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("VictoryPage/$ID") }) {
            Text(text = "Victoire")
        }
    }



}

