package com.mousescrewstudio.trailsofwonder.ui

import android.os.Parcelable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.mousescrewstudio.trailsofwonder.ui.database.Hunt
import com.mousescrewstudio.trailsofwonder.ui.database.Indice
import com.mousescrewstudio.trailsofwonder.ui.database.deleteIndice
import com.mousescrewstudio.trailsofwonder.ui.database.getIndicesFromHunt
import com.mousescrewstudio.trailsofwonder.ui.database.getUserHunts
import com.mousescrewstudio.trailsofwonder.ui.database.updateIndiceOrder
import kotlinx.parcelize.Parcelize
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Parcelize
data class IndexItem(val title: String) : Parcelable

// Page récapitulative des indices de la chasse créée
@Composable
fun IndicesRecapPage(
    huntId: String,
    onBackClick: () -> Unit,
    onAddIndexClick: () -> Unit,
    onEditIndiceClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Indices") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { onAddIndexClick() }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
            )
        }
    ) {
        innerPadding ->
        VerticalReorderList(
            huntId = huntId,
            modifier = Modifier.padding(innerPadding),
            onEditIndiceClick = onEditIndiceClick
        )
    }
}

// LazyColumn avec possibilité de changer les éléments de place en les glissant (appui long pour que ça marche)
@Composable
fun VerticalReorderList(
    huntId: String,
    modifier: Modifier = Modifier,
    onEditIndiceClick: (String) -> Unit
) {
    var huntIndices by rememberSaveable { mutableStateOf(emptyList<Indice>()) }

    getIndicesFromHunt(
        huntId = huntId,
        onSuccess = { indices ->
            huntIndices = indices
            println("Chasses récupérées avec succès")
        },
        onFailure = { exception ->
            // Erreur à gérer
            println("Erreur lors de la récupération des indices : $exception")
        }
    )

    //var indices by rememberSaveable { mutableStateOf(List(10) { IndexItem("Index $it") }) }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        huntIndices = huntIndices.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }

        // Mettre à jour l'ordre local
        huntIndices.forEachIndexed { index, indice ->
            updateIndiceOrder(indice, index)
        }

    })

    DisposableEffect(state.listState) {
        onDispose {
            // Lorsque la liste est quittée, mettez à jour l'ordre dans Firebase
            huntIndices.forEachIndexed { index, indice ->
                updateIndiceOrder(indice, index)
            }
        }
    }

    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        items(huntIndices, { it.order }) { item ->
            ReorderableItem(state, key = item.order) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                Column(
                    modifier = Modifier
                        .shadow(elevation.value)
                        .background(MaterialTheme.colors.surface)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Text with Material Theme styling
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )

                        // Edit button
                        IconButton(onClick = {
                            onEditIndiceClick(item.id)
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        }

                        // Delete button
                        IconButton(onClick = {
                            huntIndices = huntIndices.filter { it != item }

                            deleteIndice(
                                huntId = huntId,
                                indiceId = item.id,
                                onSuccess = {
                                    println("Indice supprimé avec succès")
                                },
                                onFailure = { exception ->
                                    println("Erreur lors de la suppression de l'indice dans Firestore : $exception")
                                }
                            )
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        }

                        // Draggable icon
                        Icon(
                            imageVector = Icons.Default.DragIndicator,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}





