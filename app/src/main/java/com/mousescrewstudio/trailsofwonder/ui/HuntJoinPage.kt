package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.mousescrewstudio.trailsofwonder.ui.database.Hunt
import com.mousescrewstudio.trailsofwonder.ui.database.getPublishedHunts
import java.util.UUID


@Composable
fun HuntJoinPage(
    onHuntClicked: (String) -> Unit,
    chatPage: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var tagText by remember { mutableStateOf(TextFieldValue()) }
    var tagsWithIds by remember { mutableStateOf(listOf<TagItemData>()) }
    var publishedHunts by remember { mutableStateOf(emptyList<Hunt>()) }
    var filteredPublishedHunts by remember { mutableStateOf(emptyList<Hunt>()) }
    LaunchedEffect(FirebaseAuth.getInstance().currentUser?.uid) {
        getPublishedHunts(
            onSuccess = { hunts ->
                publishedHunts = hunts
                filteredPublishedHunts = publishedHunts
                println("Chasses récupérées avec succès")
            },
            onFailure = { exception ->
                // Erreur à gérer
                println("Erreur lors de la récupération des chasses : $exception")
            }
        )
    }

    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rejoindre une chasse",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            //Button(
            //    onClick = { navController.navigate("ChatPage/DummyPerson") }
            //) {
            //    Text("Chat")
            //}

            Spacer(modifier = Modifier.height(12.dp))
            SearchBar(
                searchQuery = query,
                onSearchQueryChange = { newQuery ->
                    query = newQuery
                    filteredPublishedHunts = filterHunt(newQuery, tagsWithIds, publishedHunts)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp))
            {
                TextField(
                    value = tagText,
                    onValueChange = { tagText = it },
                    label = { Text("Tag") },
                    modifier = Modifier
                        .weight(2f)
                        .padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        if (tagText.text.isNotEmpty()) {
                            tagsWithIds = tagsWithIds + TagItemData(
                                UUID.randomUUID().toString(),
                                tagText.text
                            )
                            tagText = TextFieldValue()
                            filteredPublishedHunts = filterHunt(query, tagsWithIds, publishedHunts) // On met à jour les tags + on relance la recherche
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("Ajouter")
                }
            }


            TagsList(tags = tagsWithIds, onTagRemoveClick = { tagId ->
                tagsWithIds = tagsWithIds.filterNot { it.id == tagId }
                filteredPublishedHunts = filterHunt(query, tagsWithIds, publishedHunts)   // Si un tag est supprimé, on recharge la recherche aussi
            })

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(filteredPublishedHunts) { hunt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        onHuntClicked(hunt.id)
                    },
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = hunt.huntName, style = MaterialTheme.typography.headlineSmall)
                    Text(text = "Localisation: ${hunt.location}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Difficulté: ${getDifficultyString(hunt.difficulty)}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Durée estimée: ${hunt.durationHours}h${hunt.durationMinutes}min", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Créateur: ${hunt.creatorUsername}", style = MaterialTheme.typography.bodyMedium)

                    // Tags
                    if (hunt.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tags: ${hunt.tags.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }

    //SearchResultsHunt(publishedHunts, navController)


}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(searchQuery: String,
              onSearchQueryChange: (String) -> Unit) {
    // Pour controller le clavier
    val keyboardController by remember { mutableStateOf<SoftwareKeyboardController?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 8.dp
    ) {
        TextField(
            value = searchQuery,
            onValueChange = {
                onSearchQueryChange(it)
            },
            label = { Text("Rechercher") },
            keyboardOptions = KeyboardOptions.Default.copy( // Remplace la retour chariot par un "Done"
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) {
                        keyboardController?.show()
                    }
                }
        )
    }
}

@Composable
fun getDifficultyString(difficulty: Int): String {
    return when (difficulty) {
        0 -> "Facile"
        1 -> "Moyen"
        2 -> "Difficile"
        else -> "Inconnu"
    }
}

fun filterHunt(
    query: String,
    tagsWithIds: List<TagItemData>,
    publishedHunts: List<Hunt>
) : List<Hunt> {

    val res = publishedHunts.filter {
        it.huntName.contains(query, ignoreCase = true) //|| it.tags.contains(tagsWithIds)
    }

    val listTag = mutableListOf<String>()   // Liste des String des tags
    tagsWithIds.forEach { tags ->
        listTag.add(tags.tag)
    }


    return res.filter {
        it.tags.containsAll(listTag)
    }
}


fun generateHunts(): List<Hunt> {
    var x = mutableListOf<Hunt>()
    x = listOf(
        Hunt("Un", "Paris", 1, 1, 1, listOf<String>("1")),
        Hunt("Deux", "Londre", 1, 1, 1, listOf<String>("2")),
        Hunt("Trois", "Budapest", 1, 1, 1, listOf<String>("2")),
        Hunt("Quatre", "Fontaine", 1, 1, 1, listOf<String>("4")),
        Hunt("Cinq", "Numazu", 1, 1, 1, listOf<String>("5")),
        Hunt("Six", "Macross City", 1, 1, 1, listOf<String>("5")),
        Hunt("Septn", "Belobog", 1, 1, 1, listOf<String>("5", "1"))
    ).toMutableList()

    return x

}



@Preview(showBackground = true)
@Composable
fun PreviewHunt() {
    //HuntJoinPage()
}