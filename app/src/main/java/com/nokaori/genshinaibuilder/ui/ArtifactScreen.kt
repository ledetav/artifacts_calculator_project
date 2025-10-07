package com.nokaori.genshinaibuilder.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nokaori.genshinaibuilder.data.Artifact
import com.nokaori.genshinaibuilder.data.ArtifactSet
import com.nokaori.genshinaibuilder.data.ArtifactStat
import com.nokaori.genshinaibuilder.data.StatValue
import com.nokaori.genshinaibuilder.viewmodel.ArtifactViewModel

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
            onArtifactSetDropdownStateChanged = { artifactViewModel.onArtifactSetDropdownStateChanged(it)}
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

@Composable
fun ArtifactItem(artifact: Artifact){
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = artifact.artifactName,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "${artifact.setName} (+${artifact.level})",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "⭐".repeat(artifact.rarity.stars),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = artifact.slot.diasplayName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatStat(artifact.mainStat),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun formatStat(stat: ArtifactStat): String {
    val valueString = when (val statValue = stat.value) {
        is StatValue.IntValue -> statValue.value.toString()
        is StatValue.DoubleValue -> statValue.value.toString()
    }

    return if (stat.type.isPercentage) {
        val cleanName = stat.type.displayName.replace(" %", "")
        "$cleanName ${valueString}%"
    } else {
        "${stat.type.displayName} $valueString"
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
    onArtifactSetDropdownStateChanged: (Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.7f)
                .padding(8.dp)
        ){
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        .verticalScroll(rememberScrollState()),
                ) {
                    ArtifactSetFilterView(
                        selectedArtifactSet = selectedArtifactSet,
                        artifactSetSearchQuery = artifactSetSearchQuery,
                        isArtifactSetDropdownExpanded = isArtifactSetDropdownExpanded,
                        filteredArtifactSets = filteredArtifactSets,
                        onArtifactSetSelected = onArtifactSetSelected,
                        onArtifactSetSearchQueryChanged = onArtifactSetSearchQueryChanged,
                        onArtifactSetDropdownStateChanged = onArtifactSetDropdownStateChanged
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactSetFilterView(
    selectedArtifactSet: ArtifactSet?,
    artifactSetSearchQuery: String,
    isArtifactSetDropdownExpanded: Boolean,
    filteredArtifactSets: List<ArtifactSet>,
    onArtifactSetSelected: (ArtifactSet) -> Unit,
    onArtifactSetSearchQueryChanged: (String) -> Unit,
    onArtifactSetDropdownStateChanged: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "Сет артефакта",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = isArtifactSetDropdownExpanded,
            onExpandedChange = onArtifactSetDropdownStateChanged
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = artifactSetSearchQuery,
                onValueChange = onArtifactSetSearchQueryChanged,
                label = { Text("Выберeте сет") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isArtifactSetDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = isArtifactSetDropdownExpanded,
                onDismissRequest = { onArtifactSetDropdownStateChanged(false) }
            ) {
                filteredArtifactSets.forEach { artifactSet ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = artifactSet.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = artifactSet.name)
                            }
                        },
                        onClick = {
                            onArtifactSetSelected(artifactSet)
                        }
                    )
                }
            }
        }
    }
}