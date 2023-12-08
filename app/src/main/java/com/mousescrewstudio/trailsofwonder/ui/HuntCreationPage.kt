package com.mousescrewstudio.trailsofwonder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mousescrewstudio.trailsofwonder.ui.database.Hunt
import com.mousescrewstudio.trailsofwonder.ui.database.deleteHunt
import com.mousescrewstudio.trailsofwonder.ui.database.getHuntFromId
import com.mousescrewstudio.trailsofwonder.ui.database.predefinedTags
import com.mousescrewstudio.trailsofwonder.ui.database.publishHunt
import com.mousescrewstudio.trailsofwonder.ui.database.saveHunt
import com.mousescrewstudio.trailsofwonder.ui.database.updateHunt
import java.util.UUID

data class TagItemData(val id: String, val tag: String, var isSelected: Boolean)

// Page de création et d'édition de chasse au trésor
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuntCreationPage(
    huntId: String? = null,
    editMode: Boolean,
    onSaveClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onIndicesClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    // Si édition => on crée une top bar avec bouton de retour
    if(!editMode) {
        MainHuntCreationContent(
            huntId = huntId,
            editMode = editMode,
            onSaveClick = onSaveClick,
            onDeleteClick = onDeleteClick,
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
                    title = { Text(text = "Édition de chasse au trésor",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary) },
                )
            }
        ) { innerPadding ->
            MainHuntCreationContent(
                huntId = huntId,
                editMode = editMode,
                onSaveClick = onSaveClick,
                onDeleteClick = onDeleteClick,
                onIndicesClick = onIndicesClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

}


// Contenu commun à cette page (entre création et édition)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHuntCreationContent(
    huntId: String? = null,
    editMode: Boolean,
    onSaveClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onIndicesClick: (String) -> Unit,
    modifier: Modifier
) {
    var hunt by remember { mutableStateOf(Hunt()) }

    var huntName by remember { mutableStateOf(hunt.huntName) }
    var location by remember { mutableStateOf(hunt.location) }
    val difficultyItems = listOf("Facile", "Moyen", "Difficile")
    var difficultyExpanded by remember { mutableStateOf(false) }
    var difficultyIndex by remember { mutableIntStateOf(hunt.difficulty) }
    var durationHours by remember { mutableIntStateOf(hunt.durationHours) }
    var durationMinutes by remember { mutableIntStateOf(hunt.durationMinutes) }

    var tagsWithIds by remember { mutableStateOf(listOf<TagItemData>()) }

    // Visibilité de la chasse
    var isPublic by remember { mutableStateOf(hunt.isPublic) }

    // Si édition => On récupère la chasse avec son ID car elle est déjà créée
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
                    // Initialisation des tags avec isSelected basé sur la correspondance avec les prédéfinis
                    tagsWithIds = predefinedTags.map { predefinedTag ->
                        TagItemData(
                            id = UUID.randomUUID().toString(),
                            tag = predefinedTag,
                            isSelected = loadedHunt.tags.contains(predefinedTag)
                        )
                    }
                    println("Hunt is public : ${hunt.isPublic}")
                    isPublic = hunt.isPublic

                    println("Chasse chargée avec succès")
                }, {exception ->
                    println("Chasse non chargée : $exception")
                })
            }
        }
    } else {
        // Tags non sélectionnés par défaut
        LaunchedEffect(predefinedTags) {
            tagsWithIds = predefinedTags.map { predefinedTag ->
                TagItemData(
                    id = UUID.randomUUID().toString(),
                    tag = predefinedTag,
                    isSelected = false
                )
            }
        }
    }

    // Fonction de création de nouvelle chasse
    fun CreateHunt(onSuccess: (String) -> Unit) {
        var selectedTags: List<TagItemData> = listOf()
        tagsWithIds.forEach { tag ->
            if(tag.isSelected) {
                selectedTags = selectedTags + tag
            }
        }
        // Récupération des données de la chasse dans une seule structure
        val huntData = Hunt(
            huntName = huntName,
            location = location,
            difficulty = difficultyIndex,
            durationHours = durationHours,
            durationMinutes = durationMinutes,
            tags = selectedTags.map {it.tag},
            isPublic = isPublic
        )

        // Sauvegarde de la chasse dans Firestore
        saveHunt(huntData, onSuccess)
    }

    // Fonction de mise à jour de la chasse actuelle
    fun UpdateHunt(onSuccess: (String) -> Unit) {
        var selectedTags: List<TagItemData> = listOf()
        tagsWithIds.forEach { tag ->
            if(tag.isSelected) {
                selectedTags = selectedTags + tag
            }
        }
        // Récupération des données de la chasse dans une seule structure
        if(huntId != null) {
            val huntData = Hunt(
                id = huntId,
                huntName = huntName,
                location = location,
                difficulty = difficultyIndex,
                durationHours = durationHours,
                durationMinutes = durationMinutes,
                tags = selectedTags.map { it.tag },
                isPublic = isPublic
            )

            // Sauvegarde de la chasse dans Firestore
            //updateHunt(huntId, huntData, onSuccess)
            publishHunt(huntData, {
                updateHunt(huntId, huntData, onSuccess)
            }) { exception ->
                println("Erreur lors de la publication de la chasse ${huntData.id} : $exception")
            }
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
            TagsList(
                tags = tagsWithIds,
                onTagClick = { clickedTag ->
                    tagsWithIds = tagsWithIds.map { tag ->
                        if(tag == clickedTag) {
                            tag.copy(isSelected = !tag.isSelected)
                        } else {
                            tag
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Text(text = "Visibilité")
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Privé", modifier = Modifier.padding(end = 4.dp))
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isChecked ->
                            isPublic = isChecked
                        }
                    )
                    Text(text = "Public", modifier = Modifier.padding(start = 4.dp))
                }
            }

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
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("Indices",
                    color = MaterialTheme.colorScheme.primary)
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
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text("Sauvegarder",
                        color = MaterialTheme.colorScheme.primary)
                }

                // Boutons de suppression et de publication que en mode édition
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
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text("Supprimer",
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }


    }
}

// Liste des tags dans une ligne
@Composable
fun TagsList(
    tags: List<TagItemData>,
    onTagClick: (TagItemData) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(tags) { tag ->
            Chip(
                text = tag.tag,
                isSelected = tag.isSelected,
                onClick = { onTagClick(tag) }
            )
        }
    }
}

// Représente 1 Tag de la TagsList
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Chip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(text = text)
        },
        modifier = Modifier
            .padding(4.dp),
        selected = isSelected
    )
}

// Composant permettant de configurer le temps estimé de la chasse (heures et minutes)
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