package com.mousescrewstudio.trailsofwonder.ui.database

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

// Classe représentant un message entre deux utilisateurs
data class Message(
    val sender: String,
    val receiver: String,
    val content: String,
    val sendDate: Timestamp,
) {
    constructor() : this("", "", "", Timestamp.now())
}

// Envoi d'un message
fun sendMessage(message: Message) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("messages").add(message)
}

// Récupération des messages envoyés par un utilisateur
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
