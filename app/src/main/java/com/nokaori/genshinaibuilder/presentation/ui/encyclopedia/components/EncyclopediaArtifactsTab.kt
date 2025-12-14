package com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet

@Composable
fun EncyclopediaArtifactsTab(sets: List<ArtifactSet>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 85.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = sets,
            key = { it.name },
            contentType = { "artifact_set" }
        ) { set ->
            EncyclopediaArtifactItem(
                artifactSet = set,
                onClick = { /* TODO */ }
            )
        }
    }
}