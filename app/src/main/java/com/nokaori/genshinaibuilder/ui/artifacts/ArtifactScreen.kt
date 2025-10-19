package com.nokaori.genshinaibuilder.ui.artifacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import com.nokaori.genshinaibuilder.ui.artifacts.components.ArtifactFilterDialog
import com.nokaori.genshinaibuilder.viewmodel.ArtifactViewModel
import com.nokaori.genshinaibuilder.ui.artifacts.components.ArtifactItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactScreen(
    modifier: Modifier = Modifier,
    artifactViewModel: ArtifactViewModel = viewModel()
) {
    val searchQuery by artifactViewModel.searchQuery.collectAsState()
    val searchedArtifacts by artifactViewModel.searchedArtifacts.collectAsState()
    val isFilterDialogShown by artifactViewModel.isFilterDialogShown.collectAsState()

    if(isFilterDialogShown){
        val draftArtifactFilterState by artifactViewModel.draftArtifactFilterState.collectAsState()
        val areArtifactFiltersChanged by artifactViewModel.areArtifactFiltersChanged.collectAsState()
        val filteredArtifactSets by artifactViewModel.filteredArtifactSets.collectAsState()

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

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Артефакты",
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(onClick = { artifactViewModel.addDefaultArtifact() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить артефакт"
                )
            }
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newText ->
                artifactViewModel.onSearchQueryChange(newText)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Поиск по артефактам") },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = { artifactViewModel.onFilterIconClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Фильтр артефактов"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searchedArtifacts) {
                artifact -> ArtifactItem(artifact = artifact)
            }
        }
    }
}