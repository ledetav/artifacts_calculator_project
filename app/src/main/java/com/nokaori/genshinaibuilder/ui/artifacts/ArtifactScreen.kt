package com.nokaori.genshinaibuilder.ui.artifacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import com.nokaori.genshinaibuilder.data.ArtifactSet
import com.nokaori.genshinaibuilder.data.ArtifactSlot
import com.nokaori.genshinaibuilder.data.StatType
import com.nokaori.genshinaibuilder.viewmodel.ArtifactViewModel
import com.nokaori.genshinaibuilder.ui.artifacts.components.ArtifactItem
import com.nokaori.genshinaibuilder.ui.artifacts.components.ArtifactLevelFilter
import com.nokaori.genshinaibuilder.ui.artifacts.components.ArtifactMainStatFilter
import com.nokaori.genshinaibuilder.ui.artifacts.components.ArtifactSetFilter
import com.nokaori.genshinaibuilder.ui.artifacts.components.ArtifactSlotFilter

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
        FilterDialog(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    areFiltersChanged: Boolean,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    selectedArtifactSet: ArtifactSet?,
    artifactSetSearchQuery: String,
    isArtifactSetDropdownExpanded: Boolean,
    filteredArtifactSets: List<ArtifactSet>,
    onArtifactSetSelected: (ArtifactSet) -> Unit,
    onArtifactSetSearchQueryChanged: (String) -> Unit,
    onArtifactSetFilterDropdownDismiss: () -> Unit,
    onClearSelectedArtifactSet: () -> Unit,
    selectedArtifactLevelRange: ClosedFloatingPointRange<Float>,
    onArtifactLevelRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onLevelManualInput: (String, String) -> Unit,
    selectedArtifactSlots: Set<ArtifactSlot>,
    onArtifactSlotClicked: (ArtifactSlot) -> Unit,
    selectedArtifactMainStat: StatType?,
    onArtifactMainStatSelected: (StatType) -> Unit,
    onClearSelectedArtifactMainStat: () -> Unit
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(screenHeight * 0.7f)
                .padding(8.dp)
        ){
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Фильтры",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть фильтры"
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {

                    ArtifactSetFilter(
                        selectedArtifactSet = selectedArtifactSet,
                        artifactSetSearchQuery = artifactSetSearchQuery,
                        isArtifactSetDropdownExpanded = isArtifactSetDropdownExpanded,
                        filteredArtifactSets = filteredArtifactSets,
                        onArtifactSetSelected = onArtifactSetSelected,
                        onArtifactSetSearchQueryChanged = onArtifactSetSearchQueryChanged,
                        onArtifactSetFilterDropdownDismiss = onArtifactSetFilterDropdownDismiss,
                        onClearSelectedArtifactSet = onClearSelectedArtifactSet
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ArtifactLevelFilter(
                        artifactLevelRange = selectedArtifactLevelRange,
                        onArtifactLevelRangeChanged = onArtifactLevelRangeChanged,
                        onLevelManualInput = onLevelManualInput
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ArtifactSlotFilter(
                        selectedArtifactSlots = selectedArtifactSlots,
                        onArtifactSlotClicked = onArtifactSlotClicked
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ArtifactMainStatFilter(
                        selectedArtifactMainStat = selectedArtifactMainStat,
                        onArtifactMainStatSelected = onArtifactMainStatSelected,
                        onClearSelectedArtifactMainStat = onClearSelectedArtifactMainStat
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onReset) {
                        Text("Сбросить")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onApply,
                        enabled = areFiltersChanged
                    ) {
                        Text("Применить")
                    }
                }
            }
        }
    }
}