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
fun ArtifactScreen(artifactViewModel: ArtifactViewModel = viewModel()) {
    val searchQuery by artifactViewModel.searchQuery.collectAsState()
    val searchedArtifacts by artifactViewModel.searchedArtifacts.collectAsState()
    val isFilterDialogShown by artifactViewModel.isFilterDialogShown.collectAsState()
    val areFiltersChanged by artifactViewModel.areFiltersChanged.collectAsState()
    val selectedArtifactSet by artifactViewModel.selectedArtifactSet.collectAsState()
    val isArtifactSetDropdownExpanded by artifactViewModel.isArtifactSetDropdownExpanded.collectAsState()
    val artifactSetSearchQuery by artifactViewModel.artifactSetSearchQuery.collectAsState()
    val filteredArtifactSets by artifactViewModel.filteredArtifactSets.collectAsState()
    val selectedArtifactLevelRange by artifactViewModel.selectedArtifactLevelRange.collectAsState()
    val selectedArtifactSlots by artifactViewModel.selectedArtifactSlots.collectAsState()
    val selectedArtifactMainStat by artifactViewModel.selectedArtifactMainStat.collectAsState()

    if(isFilterDialogShown){
        ArtifactFilterDialog(
            areFiltersChanged = areFiltersChanged,
            onDismiss = artifactViewModel::onFilterDialogDismiss,
            onApply = artifactViewModel::onApplyFilters,
            onReset = artifactViewModel::onResetFilters,
            selectedArtifactSet = selectedArtifactSet,
            artifactSetSearchQuery = artifactSetSearchQuery,
            isArtifactSetDropdownExpanded = isArtifactSetDropdownExpanded,
            filteredArtifactSets = filteredArtifactSets,
            onArtifactSetSelected = { artifactViewModel.onArtifactSetSelected(it) },
            onArtifactSetSearchQueryChanged = { artifactViewModel.onArtifactSetSearchQueryChanged(it)},
            onArtifactSetFilterDropdownDismiss = artifactViewModel::onArtifactSetFilterDropdownDismiss,
            onClearSelectedArtifactSet = artifactViewModel::onClearSelectedArtifactSet,
            selectedArtifactLevelRange = selectedArtifactLevelRange,
            onArtifactLevelRangeChanged = { artifactViewModel.onLevelRangeChanged(it) },
            onLevelManualInput = { from, to -> artifactViewModel.onLevelManualInputChanged(from, to)},
            selectedArtifactSlots = selectedArtifactSlots,
            onArtifactSlotClicked = { artifactViewModel.onArtifactSlotClicked(it) },
            selectedArtifactMainStat = selectedArtifactMainStat,
            onArtifactMainStatSelected = { artifactViewModel.onArtifactMainStatSelected(it) },
            onClearSelectedArtifactMainStat = artifactViewModel::onClearSelectedArtifactMainStat
        )
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Артефакты",
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(onClick = { artifactViewModel.addDefaultaArtifact() }) {
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