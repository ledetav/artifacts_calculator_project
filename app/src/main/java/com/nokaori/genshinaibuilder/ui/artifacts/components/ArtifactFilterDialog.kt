package com.nokaori.genshinaibuilder.ui.artifacts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nokaori.genshinaibuilder.data.ArtifactSet
import com.nokaori.genshinaibuilder.data.ArtifactSlot
import com.nokaori.genshinaibuilder.data.StatType

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