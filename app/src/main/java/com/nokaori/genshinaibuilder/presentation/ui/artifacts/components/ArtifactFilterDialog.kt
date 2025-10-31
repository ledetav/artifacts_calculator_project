package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.data.ArtifactFilterState
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactFilterDialog(
    artifactFilterState: ArtifactFilterState,
    areArtifactFiltersChanged: Boolean,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    filteredArtifactSets: List<ArtifactSet>,
    onArtifactSetSelected: (ArtifactSet) -> Unit,
    onArtifactSetSearchQueryChanged: (String) -> Unit,
    onArtifactSetDropdownExpandedChange: (Boolean) -> Unit,
    onClearSelectedArtifactSet: () -> Unit,
    onArtifactLevelRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onLevelManualInput: (String, String) -> Unit,
    onArtifactSlotClicked: (ArtifactSlot) -> Unit,
    onArtifactMainStatSelected: (StatType) -> Unit,
    onClearSelectedArtifactMainStat: () -> Unit
) {
    BaseDialog(
        onDismissRequest = onDismiss,
        title = "Фильтры",
        content = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {

                ArtifactSetFilter(
                    selectedArtifactSet = artifactFilterState.selectedArtifactSet,
                    artifactSetSearchQuery = artifactFilterState.artifactSetSearchQuery,
                    isArtifactSetDropdownExpanded = artifactFilterState.isArtifactSetDropdownExpanded,
                    filteredArtifactSets = filteredArtifactSets,
                    onArtifactSetSelected = onArtifactSetSelected,
                    onArtifactSetSearchQueryChanged = onArtifactSetSearchQueryChanged,
                    onArtifactSetDropdownExpandedChange = onArtifactSetDropdownExpandedChange,
                    onClearSelectedArtifactSet = onClearSelectedArtifactSet
                )

                Spacer(modifier = Modifier.height(16.dp))

                ArtifactLevelFilter(
                    artifactLevelRange = artifactFilterState.selectedArtifactLevelRange,
                    onArtifactLevelRangeChanged = onArtifactLevelRangeChanged,
                    onLevelManualInput = onLevelManualInput
                )

                Spacer(modifier = Modifier.height(16.dp))

                ArtifactSlotFilter(
                    selectedArtifactSlots = artifactFilterState.selectedArtifactSlots,
                    onArtifactSlotClicked = onArtifactSlotClicked
                )

                Spacer(modifier = Modifier.height(16.dp))

                ArtifactMainStatFilter(
                    selectedArtifactMainStat = artifactFilterState.selectedArtifactMainStat,
                    onArtifactMainStatSelected = onArtifactMainStatSelected,
                    onClearSelectedArtifactMainStat = onClearSelectedArtifactMainStat
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        actions = {
            TextButton(onClick = onReset) {
                Text("Сбросить")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onApply,
                enabled = areArtifactFiltersChanged
            ) {
                Text("Применить")
            }
        }
    )
}