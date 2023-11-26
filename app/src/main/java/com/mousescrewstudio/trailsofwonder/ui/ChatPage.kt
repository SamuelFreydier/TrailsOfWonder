package com.mousescrewstudio.trailsofwonder.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mousescrewstudio.trailsofwonder.ui.database.GetAllMessages
import com.mousescrewstudio.trailsofwonder.ui.database.Message
import com.mousescrewstudio.trailsofwonder.ui.database.sendMessage


// Page de discussion entre deux utilisateurs
@SuppressLint("UnrememberedMutableState")
@Composable
fun ChatPage(
    receiverId: String
) {

    val senderId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val senderUsername = FirebaseAuth.getInstance().currentUser?.displayName.toString()
    var receiverUsername = receiverId

    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }

    LaunchedEffect(FirebaseAuth.getInstance().currentUser?.uid) {
        GetAllMessages(senderId) { loadedMessages ->
            messages = loadedMessages
        }
        GetAllMessages(receiverId) { loadedMessages ->
            messages = messages + loadedMessages
        }

        val firestore = FirebaseFirestore.getInstance()
        val fireCollection = firestore.collection("username")
        val fireDocument = fireCollection.document("UsernameList")

        fireDocument
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val data = document.data
                    if (data != null) {
                        for ((key, value) in data) {
                            println("key : $key & value : $value & receiverId : $receiverId")
                            if (key == receiverId) {
                                receiverUsername = value.toString()
                                println("trouvé $receiverUsername")
                            }
                            else println("Non trouvé")
                        }
                    } else println("Pas ici")
                } else println("Non plus")
            }
    }

    println(receiverUsername)




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chat avec $receiverUsername ($receiverId)",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Entrée de texte pour envoyer un message
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Écrire un message...") }
            )

            Button(
                onClick = {
                    // Envoyer le message
                    sendMessage(Message(senderId, receiverId, messageText))
                    // Effacer le champ de texte
                    messageText = ""
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Envoyer")
            }
        }

        // Box du chat
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .border(BorderStroke(5.dp, Color.Blue))
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                AfficherMessage(messages, receiverId, receiverUsername)
            }
        }
    }
}


@Composable
fun AfficherMessage(list : List<Message>, receiverId: String, receiverUsername : String) {

 var color = Color.Black

 LazyColumn(
     modifier = Modifier
         .fillMaxWidth()
         .clip(shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
 ) {
     items(list) { msg ->

         var sender = msg.sender


         if(msg.receiver == receiverId) {    // Envoyé par l'autre
             color = Color.Yellow
             sender = receiverUsername
         }
         else {
             color = Color.LightGray // Envoyé par toi-même
             sender = FirebaseAuth.getInstance().currentUser?.displayName.toString()
         }

         Box(
             modifier = Modifier
                 .padding(16.dp)
                 .clip(shape = RoundedCornerShape(10.dp))
                 .background(color)
         ) {
             Text(
                 text = AnnotatedString("'${sender}' : ${msg.content}")
             )
         }
     }
 }
}


@Preview
@Composable
fun Preview() {
}



