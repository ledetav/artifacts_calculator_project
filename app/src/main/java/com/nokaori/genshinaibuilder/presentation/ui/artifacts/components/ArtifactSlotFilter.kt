package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.runtime.Composable
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.presentation.ui.common.Orientation
import com.nokaori.genshinaibuilder.presentation.ui.common.components.IconToggleButton
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MultiSelectToggleButtonGroup

@Composable
fun ArtifactSlotFilter(
    selectedArtifactSlots: Set<ArtifactSlot>,
    onArtifactSlotClicked: (ArtifactSlot) -> Unit,
    orientation: Orientation = Orientation.HORIZONTAL
) {
    fun getIconForSlot(slot: ArtifactSlot) = when (slot) {
        ArtifactSlot.FLOWER_OF_LIFE -> Icons.Default.LocalFlorist
        ArtifactSlot.PLUME_OF_DEATH -> Icons.Default.Edit
        ArtifactSlot.SANDS_OF_EON -> Icons.Default.HourglassEmpty
        ArtifactSlot.GOBLET_OF_EONOTHEM -> Icons.Default.WineBar
        ArtifactSlot.CIRCLET_OF_LOGOS -> Icons.Default.School
    }

    MultiSelectToggleButtonGroup(
        title = "Слот",
        items = ArtifactSlot.entries,
        selectedItems = selectedArtifactSlots,
        onItemClick = onArtifactSlotClicked,
        orientation = orientation
    ) { slot, isSelected ->
        IconToggleButton(
            onClick = { onArtifactSlotClicked(slot) },
            isSelected = isSelected,
            icon = getIconForSlot(slot),
            contentDescription = slot.displayName
        )
    }
}