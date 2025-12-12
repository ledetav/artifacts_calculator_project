package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.presentation.ui.common.Orientation
import com.nokaori.genshinaibuilder.presentation.ui.common.components.IconToggleButton
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MultiSelectToggleButtonGroup
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName
import coil3.compose.rememberAsyncImagePainter
import com.nokaori.genshinaibuilder.presentation.util.YattaAssets

@Composable
fun ArtifactSlotFilter(
    selectedArtifactSlots: Set<ArtifactSlot>,
    onArtifactSlotClicked: (ArtifactSlot) -> Unit,
    orientation: Orientation = Orientation.HORIZONTAL
) {
    MultiSelectToggleButtonGroup(
        title = stringResource(R.string.filter_artifact_slot),
        items = ArtifactSlot.entries,
        selectedItems = selectedArtifactSlots,
        onItemClick = onArtifactSlotClicked,
        orientation = orientation
    ) { slot, isSelected ->

        val iconUrl = YattaAssets.getArtifactSlotIconUrl(slot)
        val painter = rememberAsyncImagePainter(iconUrl)

        IconToggleButton(
            onClick = { onArtifactSlotClicked(slot) },
            isSelected = isSelected,
            painter = painter,
            contentDescription = slot.toDisplayName(),
            activeContentColor = Color.Unspecified,
            inactiveContentColor = Color.Unspecified
        )
    }
}