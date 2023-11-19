package com.mousescrewstudio.trailsofwonder.ui.database

import androidx.compose.runtime.Composable
import com.google.firebase.firestore.FirebaseFirestore

data class Message(
    val sender: String,
    val receiver: String,
    val content: String,
) {
}

fun sendMessage(message: Message) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("messages").add(message)
}

@Composable
fun GetAllMessages(senderId: String, receiverId: String, onSucess: (ArrayList<String>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("messages")
        //.whereEqualTo("senderId", senderId)
        //.whereEqualTo("receiverId", receiverId)
        .addSnapshotListener { snapshot, e ->
            if (e != null) {
                println("Erreur")
                return@addSnapshotListener
            }

            val messages = ArrayList<String>()
            for (doc in snapshot!!) {
                doc.getString("content")?.let {
                    messages.add(it)
                }
                val value = doc.data.values
                println("value $value")
            }
            onSucess(messages)

        }
}
