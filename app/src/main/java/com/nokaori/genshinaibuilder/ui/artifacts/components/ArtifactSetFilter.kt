package com.nokaori.genshinaibuilder.ui.artifacts.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.data.ArtifactSet
import com.nokaori.genshinaibuilder.ui.common.components.SearchableExposedDropdown
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactSetFilter(
    selectedArtifactSet: ArtifactSet?,
    artifactSetSearchQuery: String,
    isArtifactSetDropdownExpanded: Boolean,
    filteredArtifactSets: List<ArtifactSet>,
    onArtifactSetSelected: (ArtifactSet) -> Unit,
    onArtifactSetSearchQueryChanged: (String) -> Unit,
    onArtifactSetFilterDropdownDismiss: () -> Unit,
    onClearSelectedArtifactSet: () -> Unit
) {
    Column {
        Text(
            text = "Сет артефакта",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SearchableExposedDropdown(
            modifier = Modifier.fillMaxWidth(),
            label = "Выберите сет артефакта",
            searchQuery = artifactSetSearchQuery,
            onSearchQueryChange = onArtifactSetSearchQueryChanged,
            isExpanded = isArtifactSetDropdownExpanded,
            onExpandedChange = {
                if (!it) onArtifactSetFilterDropdownDismiss()
            },
            onDismiss = onArtifactSetFilterDropdownDismiss,
            selectedValueText = selectedArtifactSet?.name ?: "",
            onClear = onClearSelectedArtifactSet
        ) {
            if (filteredArtifactSets.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Ничего не найдено") },
                    onClick = {},
                    enabled = false
                )
            } else {
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