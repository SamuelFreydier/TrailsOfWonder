package com.mousescrewstudio.trailsofwonder.ui.database

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

// Classe représentant un commentaire sur une chasse publiée
data class Comment(
    var commentId: String = "",
    var userId: String = "",
    var username: String = "",
    var comment: String = "",
    var timestamp: Timestamp = Timestamp.now()
)

// Récupère les commentaires d'une chasse
fun getCommentsForHunt(huntId: String, onSuccess: (List<Comment>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("publishedHunts")
        .document(huntId)
        .collection("comments")
        .get()
        .addOnSuccessListener { result ->
            val comments = result.toObjects(Comment::class.java)
            val updatedComments = comments.map {
                it.copy(commentId = result.documents[comments.indexOf(it)].id)
            }
            onSuccess(updatedComments)
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

// Crée un commentaire
fun saveComment(huntId: String, commentText: String) {
    val user = FirebaseAuth.getInstance().currentUser

    if( user != null ) {
        var newComment: Comment = Comment(
            userId = user.uid,
            username = user.displayName.toString(),
            timestamp = Timestamp.now(),
            comment = commentText
        )

        db.collection("publishedHunts")
            .document(huntId)
            .collection("comments")
            .add(newComment)
    }
}