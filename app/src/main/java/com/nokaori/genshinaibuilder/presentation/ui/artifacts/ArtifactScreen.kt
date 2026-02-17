package com.nokaori.genshinaibuilder.presentation.ui.artifacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.components.ArtifactFilterDialog
import com.nokaori.genshinaibuilder.presentation.viewmodel.ArtifactViewModel
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.components.ArtifactItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactScreen(
    modifier: Modifier = Modifier,
    artifactViewModel: ArtifactViewModel,
    onArtifactClick: (Int) -> Unit
) {
    val searchQuery by artifactViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchedArtifacts by artifactViewModel.searchedArtifacts.collectAsStateWithLifecycle()
    val isFilterDialogShown by artifactViewModel.isFilterDialogShown.collectAsStateWithLifecycle()

    if(isFilterDialogShown){
        val draftArtifactFilterState by artifactViewModel.draftArtifactFilterState.collectAsStateWithLifecycle()
        val areArtifactFiltersChanged by artifactViewModel.areArtifactFiltersChanged.collectAsStateWithLifecycle()
        val filteredArtifactSets by artifactViewModel.filteredArtifactSets.collectAsStateWithLifecycle()

        ArtifactFilterDialog(
            artifactFilterState = draftArtifactFilterState,
            areArtifactFiltersChanged = areArtifactFiltersChanged,
            onDismiss = artifactViewModel::onFilterDialogDismiss,
            onApply = artifactViewModel::onApplyFilters,
            onReset = artifactViewModel::onResetFilters,
            filteredArtifactSets = filteredArtifactSets,
            onArtifactSetSelected = { artifactViewModel.onArtifactSetSelected(it) },
            onArtifactSetSearchQueryChanged = { artifactViewModel.onArtifactSetSearchQueryChanged(it)},
            onArtifactSetDropdownExpandedChange = artifactViewModel::onArtifactSetFilterDropdownExpandedChanged,
            onClearSelectedArtifactSet = artifactViewModel::onClearSelectedArtifactSet,
            onArtifactLevelRangeChanged = { artifactViewModel.onLevelRangeChanged(it) },
            onLevelManualInput = { from, to -> artifactViewModel.onLevelManualInputChanged(from, to)},
            onArtifactSlotClicked = { artifactViewModel.onArtifactSlotClicked(it) },
            onArtifactMainStatSelected = { artifactViewModel.onArtifactMainStatSelected(it) },
            onClearSelectedArtifactMainStat = artifactViewModel::onClearSelectedArtifactMainStat
        )
    }

    Column(modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { newText ->
                artifactViewModel.onSearchQueryChange(newText)
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            label = { Text(stringResource(R.string.artifact_search_placeholder)) },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = { artifactViewModel.onFilterIconClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filter_dialog_title)
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp), 
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                searchedArtifacts, 
                key = { it.id } 
            ) { artifact ->
                ArtifactItem(
                    artifact = artifact,
                    onClick = { onArtifactClick(artifact.id) }
                )
            }
        }
    }
}