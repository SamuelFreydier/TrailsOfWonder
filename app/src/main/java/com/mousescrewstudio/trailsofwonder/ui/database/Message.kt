package com.mousescrewstudio.trailsofwonder.ui.database

import com.google.firebase.firestore.FirebaseFirestore

data class Message(
    val sender: String,
    val receiver: String,
    val content: String,
) {
    constructor() : this("", "", "")
}
fun sendMessage(message: Message) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("messages").add(message)
}


fun GetAllMessages(senderId: String, onSucess: (MutableList<Message>)/*(ArrayList<String>)*/ -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("messages")
        .whereEqualTo("sender", senderId)
        .addSnapshotListener { snapshot, e ->
            if (e != null) {
                println("Erreur")
                return@addSnapshotListener
            }

            var messages = mutableListOf<Message>()
            if (snapshot != null && !snapshot.isEmpty) {
                val m = snapshot.toObjects(Message::class.java)
                messages = m
            }

            onSucess(messages)

        }
}
