package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mousescrewstudio.trailsofwonder.ui.database.Hunt
import com.mousescrewstudio.trailsofwonder.ui.database.deleteHunt
import com.mousescrewstudio.trailsofwonder.ui.database.getHuntFromId
import com.mousescrewstudio.trailsofwonder.ui.database.saveHunt
import com.mousescrewstudio.trailsofwonder.ui.database.updateHunt
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuntCreationPage(
    huntId: String? = null,
    editMode: Boolean,
    onSaveClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onPublishClick: () -> Unit,
    onIndicesClick: (String) -> Unit,
    onBackClick: () -> Unit
) {


    if(!editMode) {
        MainHuntCreationContent(
            huntId = huntId,
            editMode = editMode,
            onSaveClick = onSaveClick,
            onDeleteClick = onDeleteClick,
            onPublishClick = onPublishClick,
            onIndicesClick = onIndicesClick,
            modifier = Modifier
        )
    } else {
        Scaffold (
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    title = { Text(text = "Édition de chasse au trésor") },
                )
            }
        ) { innerPadding ->
            MainHuntCreationContent(
                huntId = huntId,
                editMode = editMode,
                onSaveClick = onSaveClick,
                onDeleteClick = onDeleteClick,
                onPublishClick = onPublishClick,
                onIndicesClick = onIndicesClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHuntCreationContent(
    huntId: String? = null,
    editMode: Boolean,
    onSaveClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onPublishClick: () -> Unit,
    onIndicesClick: (String) -> Unit,
    modifier: Modifier
) {
    var hunt by remember { mutableStateOf(Hunt()) }

    var huntName by remember { mutableStateOf(hunt.huntName ?: "") }
    var location by remember { mutableStateOf(hunt.location ?: "") }
    val difficultyItems = listOf("Facile", "Moyen", "Difficile")
    var difficultyExpanded by remember { mutableStateOf(false) }
    var difficultyIndex by remember { mutableIntStateOf(hunt.difficulty ?: 0) }
    var durationHours by remember { mutableIntStateOf(hunt.durationHours ?: 0) }
    var durationMinutes by remember { mutableIntStateOf(hunt.durationMinutes ?: 0) }

    var tagText by remember { mutableStateOf(TextFieldValue()) }
    var tagsWithIds by remember { mutableStateOf(listOf<TagItemData>()) }

    if (editMode) {
        LaunchedEffect(huntId) {
            // Chargez l'indice à partir de Firestore
            huntId?.let {
                getHuntFromId(it, { loadedHunt ->
                    hunt = loadedHunt
                    huntName = hunt.huntName
                    location = hunt.location
                    difficultyIndex = hunt.difficulty
                    durationHours = hunt.durationHours
                    durationMinutes = hunt.durationMinutes
                    hunt.tags.forEachIndexed { _, s ->
                        tagsWithIds = tagsWithIds + TagItemData(UUID.randomUUID().toString(), s)
                    }

                    println("Chasse chargée avec succès")
                }, {exception ->
                    println("Chasse non chargée : $exception")
                })
            }
        }
    }

    fun CreateHunt(onSuccess: (String) -> Unit) {
        // Récupération des données de la chasse dans une seule structure
        val huntData = Hunt(
            huntName = huntName,
            location = location,
            difficulty = difficultyIndex,
            durationHours = durationHours,
            durationMinutes = durationMinutes,
            tags = tagsWithIds.map { it.tag }
        )

        // Sauvegarde de la chasse dans Firestore
        saveHunt(huntData, onSuccess)
    }

    fun UpdateHunt(onSuccess: (String) -> Unit) {
        // Récupération des données de la chasse dans une seule structure
        val huntData = Hunt(
            huntName = huntName,
            location = location,
            difficulty = difficultyIndex,
            durationHours = durationHours,
            durationMinutes = durationMinutes,
            tags = tagsWithIds.map { it.tag }
        )

        // Sauvegarde de la chasse dans Firestore
        if (huntId != null) {
            updateHunt(huntId, huntData, onSuccess)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            if(!editMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Création de chasse au trésor",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }


            TextField(
                value = huntName,
                onValueChange = { huntName = it },
                label = { Text("Nom") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lieu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Text(text = "Difficulté")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopStart),
            ) {
                Text(
                    difficultyItems[difficultyIndex],
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { difficultyExpanded = true })
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
                DropdownMenu(
                    expanded = difficultyExpanded,
                    onDismissRequest = { difficultyExpanded = false },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    difficultyItems.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            onClick = {
                                difficultyIndex = index
                                difficultyExpanded = false
                            },
                            text = { Text(text = s) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DurationPicker(
                    hours = durationHours,
                    minutes = durationMinutes,
                    onHoursChange = { durationHours = it },
                    onMinutesChange = { durationMinutes = it }
                )
            }

            // Tags
            TextField(
                value = tagText,
                onValueChange = { tagText = it },
                label = { Text("Tag") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    if (tagText.text.isNotEmpty()) {
                        tagsWithIds = tagsWithIds + TagItemData(UUID.randomUUID().toString(), tagText.text.toString())
                        tagText = TextFieldValue()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Ajouter Tag")
            }


            TagsList(tags = tagsWithIds, onTagRemoveClick = { tagId ->
                tagsWithIds = tagsWithIds.filterNot { it.id == tagId }
            })

            Spacer(modifier = Modifier.height(16.dp))

            // Indices button
            Button(
                onClick = {
                    if(!editMode) {
                        CreateHunt(onIndicesClick)
                    } else {
                        UpdateHunt(onIndicesClick)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Indices")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if(!editMode) {
                            CreateHunt(onSaveClick)
                        } else {
                            UpdateHunt(onSaveClick)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(end = 8.dp)
                ) {
                    Text("Sauvegarder")
                }

                // Bouton de suppression que en mode édition
                if(editMode) {
                    Button(
                        onClick = {
                            if (huntId != null) {
                                deleteHunt(huntId, onDeleteClick) { exception ->
                                    println("Erreur rencontrée lors de la suppression de la chasse : $exception")
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Supprimer")
                    }
                }

                Button(
                    onClick = { onPublishClick() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(start = 8.dp)
                ) {
                    Text("Publier")
                }
            }
        }


    }
}

@Composable
fun TagsList(tags: List<TagItemData>, onTagRemoveClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags) { tag ->
            key(tag.id) {
                TagItem(tag = tag, onRemoveClick = { onTagRemoveClick(it) })
            }
        }
    }
}

@Composable
fun TagItem(tag: TagItemData, onRemoveClick: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = tag.tag, color = MaterialTheme.colorScheme.primary)
        IconButton(onClick = { onRemoveClick(tag.id) }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }
    }
}

data class TagItemData(val id: String, val tag: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationPicker(
    hours: Int,
    minutes: Int,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = hours.toString(),
            onValueChange = {
                val newHours = if (it.isEmpty()) 0 else it.toInt()
                onHoursChange(newHours)
            },
            label = { Text("Hours") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.width(80.dp)
        )

        OutlinedTextField(
            value = minutes.toString(),
            onValueChange = {
                val newMinutes = if (it.isEmpty()) 0 else it.toInt()
                onMinutesChange(newMinutes)
            },
            label = { Text("Minutes") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.width(80.dp)
        )
    }
}