package com.nokaori.genshinaibuilder.ui.artifacts.components

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
import com.nokaori.genshinaibuilder.data.ArtifactSet
import com.nokaori.genshinaibuilder.data.ArtifactSlot
import com.nokaori.genshinaibuilder.data.StatType
import com.nokaori.genshinaibuilder.ui.common.components.BaseDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactFilterDialog(
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
    BaseDialog(
        onDismissRequest = onDismiss,
        title = "Фильтры",
        content = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
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
        },
        actions = {
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
    )
}