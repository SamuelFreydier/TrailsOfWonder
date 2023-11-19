package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mousescrewstudio.trailsofwonder.ui.database.Hunt
import com.mousescrewstudio.trailsofwonder.ui.database.getAllHunt
import java.util.UUID


@Composable
fun HuntJoinPage(
    navController: NavController,
    chatPage: () -> Unit
) {

    var data by remember { mutableStateOf(generateHunts()) }

    var query by remember { mutableStateOf("") }
    var tagText by remember { mutableStateOf(TextFieldValue()) }
    var tagsWithIds by remember { mutableStateOf(listOf<TagItemData>()) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text(
            text = "Joindre une chasse au trésor",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Button(
            onClick = { navController.navigate("ChatPage/DummyPerson") }
        ) {
            Text("Chat")
        }

        Spacer(modifier = Modifier.height(12.dp))
        SearchBar(
            searchQuery = query,
            onSearchQueryChange = { newQuery ->
                query = newQuery
                data = filterHunt(newQuery, tagsWithIds)
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

                        data = filterHunt(
                            query,
                            tagsWithIds
                        )   // On met à jour les tags + on relance la recherche
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
            data = filterHunt(query, tagsWithIds)   // Si un tag est supprimé, on recharge la recherche aussi
        })

        Spacer(modifier = Modifier.height(16.dp))

        SearchResultsHunt(data, navController)
    }
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
fun SearchResultsHunt(
    listHunt: List<Hunt>,
    navController: NavController
) {

    var huntInfo by remember { mutableStateOf("Aucun texte") }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        items(listHunt) { hunt ->

            huntInfo = "Chasse '${hunt.huntName}' située à ${hunt.location}"
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = AnnotatedString(huntInfo),
                    modifier = Modifier
                        .clickable {
                            navController.navigate("HuntSummary/${hunt.huntName}")
                        }
                        .padding(16.dp),
                )
            }
        }
    }
}



fun filterHunt(query: String, tagsWithIds: List<TagItemData>) : List<Hunt>{

    val res = generateHunts().filter {
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

    getAllHunt (
        onSuccess = { hunts ->
            x = hunts
            println("Chasses récupérées avec succès $x")
        },
        onFailure = {
            x = listOf(
                Hunt("Un", "Paris", 1, 1, 1, listOf<String>("1")),
                Hunt("Deux", "Londre", 1, 1, 1, listOf<String>("2")),
                Hunt("Trois", "Budapest", 1, 1, 1, listOf<String>("2")),
                Hunt("Quatre", "Fontaine", 1, 1, 1, listOf<String>("4")),
                Hunt("Cinq", "Numazu", 1, 1, 1, listOf<String>("5")),
                Hunt("Six", "Macross City", 1, 1, 1, listOf<String>("5")),
                Hunt("Septn", "Belobog", 1, 1, 1, listOf<String>("5", "1"))
            ).toMutableList()
        }
    )

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