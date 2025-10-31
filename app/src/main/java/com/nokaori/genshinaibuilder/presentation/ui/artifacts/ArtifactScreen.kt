package com.nokaori.genshinaibuilder.presentation.ui.artifacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.components.ArtifactFilterDialog
import com.nokaori.genshinaibuilder.presentation.viewmodel.ArtifactViewModel
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.components.ArtifactItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactScreen(
    modifier: Modifier = Modifier,
    artifactViewModel: ArtifactViewModel
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

    Column(modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        OutlinedTextField(
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
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(searchedArtifacts) {
                artifact -> ArtifactItem(artifact = artifact)
            }
        }
    }
}