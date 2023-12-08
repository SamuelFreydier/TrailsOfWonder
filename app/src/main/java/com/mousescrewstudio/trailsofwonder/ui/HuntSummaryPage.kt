package com.mousescrewstudio.trailsofwonder.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.mousescrewstudio.trailsofwonder.ui.database.Comment
import com.mousescrewstudio.trailsofwonder.ui.database.Hunt
import com.mousescrewstudio.trailsofwonder.ui.database.getCommentsForHunt
import com.mousescrewstudio.trailsofwonder.ui.database.getPublishedHuntById
import com.mousescrewstudio.trailsofwonder.ui.database.saveComment
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Page descriptive d'une chasse publiée
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuntSummaryPage(
    huntId: String,
    onHuntStart: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var hunt by remember { mutableStateOf<Hunt?>(null) }
    var commentText by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf(emptyList<Comment>()) }

    LaunchedEffect(huntId) {
        // Récupérer la chasse à partir de Firebase avec l'ID
        getPublishedHuntById(
            huntId = huntId,
            onSuccess = { fetchedHunt ->
                hunt = fetchedHunt
                println("Chasse récupérée avec succès")
            },
            onFailure = { exception ->
                // Gérer l'erreur de récupération de la chasse
                println("Erreur lors de la récupération de la chasse : $exception")
            }
        )

        // Récupérer les commentaires à partir de Firebase
        getCommentsForHunt(huntId, { fetchedComments ->
            comments = fetchedComments
        }, { exception ->
            println("Erreur rencontrée lors de la récupération des commentaires : $exception")
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = hunt?.huntName.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                // Informations sur le créateur
                Text(
                    text = buildAnnotatedString {
                        append("Créée par ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(hunt?.creatorUsername.orEmpty())
                        }
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )

                // Informations sur la chasse
                BoldTextWithColon("Localisation", hunt?.location.orEmpty())
                BoldTextWithColon("Difficulté", getDifficultyString(hunt?.difficulty ?: 0))
                BoldTextWithColon("Durée estimée", "${hunt?.durationHours}h${hunt?.durationMinutes}min")

                // Tags
                if (!hunt?.tags.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    BoldTextWithColon("Tags", hunt?.tags?.joinToString(", ") ?: "", color = MaterialTheme.colorScheme.secondary)

                }
            }

            item {
                // Bouton Commencer
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Naviguer vers la page de début de la chasse
                        //navController.navigate("StartHuntPage/${huntId}")
                        hunt?.let { onHuntStart(it.id) } // Appeler la fonction de démarrage de la chasse
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text("Commencer",
                        color = MaterialTheme.colorScheme.primary)
                }
            }

            // Section Commentaires
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Commentaires", style = MaterialTheme.typography.headlineSmall)

                // Champ de texte pour écrire un commentaire
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Ajouter un commentaire") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                // Enregistrer le commentaire dans Firebase
                                saveComment(huntId, commentText)
                                // Effacer le champ de texte après l'envoi du commentaire
                                commentText = ""
                                getCommentsForHunt(huntId, { fetchedComments ->
                                    comments = fetchedComments
                                }, { exception ->
                                    println("Erreur rencontrée lors de la récupération des commentaires : $exception")
                                })
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            // Liste défilante des commentaires
            items(comments) { comment ->
                CommentItem(comment = comment)
                Divider() // Ajouter une ligne de séparation entre les commentaires
            }
        }
    }
}

// Représente un commentaire
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentItem(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Nom de l'utilisateur et date du commentaire
        Text(
            text = "${comment.username} • ${formatTimestamp(comment.timestamp)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        // Texte du commentaire
        Text(text = comment.comment, style = MaterialTheme.typography.bodyMedium)
    }
}

// Fonction pour formater la date et l'heure à partir du timestamp
@RequiresApi(Build.VERSION_CODES.O)
fun formatTimestamp(timestamp: Timestamp): String {
    val instant = timestamp.toDate().toInstant()
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return localDateTime.format(formatter)
}

// Fonction de formatage de text pour afficher des éléments en gras
@Composable
fun BoldTextWithColon(fieldName: String, value: String, color: Color = Color.Black) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                append("$fieldName:")
            }
            append(" $value")
        },
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}