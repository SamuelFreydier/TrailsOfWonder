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
import com.mousescrewstudio.trailsofwonder.ui.database.GetAllMessages
import com.mousescrewstudio.trailsofwonder.ui.database.Message
import com.mousescrewstudio.trailsofwonder.ui.database.sendMessage


@SuppressLint("UnrememberedMutableState")
@Composable
fun ChatPage(
    receiverId: String
) {

    var messageText by remember { mutableStateOf("") }
    var mess = ArrayList<String>()

    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    messages = generateMessage()
    /*var messages = mutableListOf<Message>()
    generateMessage().forEach {
        messages.add(it)
    }*/



    val senderId = FirebaseAuth.getInstance().currentUser?.uid.toString()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chat avec $receiverId",
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
                    val newMessage = Message(senderId, receiverId, messageText)
                    sendMessage(newMessage)

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
                // Reception des message
                GetAllMessages(senderId, receiverId) { loadedMessages ->
                    mess = loadedMessages
                    println("mess $mess")
                }
                // C'est ca qui fonctionne pas, parce qu'il est appelé avant getMessages, et est vide

                LazyColumn {
                    items(mess) { message ->
                        Text(message)
                        println("message recu : $message")
                    }
                }

                AfficherMessage(messages, receiverId)
                println(messages)
            }
        }
        AfficherMessage(messages, receiverId)
    }
}

@Composable
fun AfficherMessage(list : List<Message>, receiverId: String) {

    var color = Color.Black

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
    ) {
        items(list) { msg ->

            if(msg.receiver == receiverId)
               color = Color.Yellow
            else color = Color.LightGray

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color)
            ) {
                Text(
                    text = AnnotatedString("'${msg.receiver}' : ${msg.content}")
                )
            }
        }
    }
}

fun generateMessage(): MutableList<Message> {
    return listOf(
        Message("DummyPerson", "2", "aled"),
        Message("2", "DummyPerson", "oscour"),
        Message("DummyPerson", "2", "jveuxuncouto"),
        Message("2", "DummyPerson", "ouunecorde")
    ).toMutableList()
}

@Preview
@Composable
fun Preview() {
    AfficherMessage(list = generateMessage(), "DummyPerson")
}



