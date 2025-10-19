package com.nokaori.genshinaibuilder.ui.artifacts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.data.ArtifactSlot

@Composable
fun ArtifactSlotFilter(
    selectedArtifactSlots: Set<ArtifactSlot>,
    onArtifactSlotClicked: (ArtifactSlot) -> Unit
) {
    fun getIconForSlot(slot: ArtifactSlot) = when (slot) {
        ArtifactSlot.FLOWER_OF_LIFE -> Icons.Default.LocalFlorist
        ArtifactSlot.PLUME_OF_DEATH -> Icons.Default.Edit
        ArtifactSlot.SANDS_OF_EON -> Icons.Default.HourglassEmpty
        ArtifactSlot.GOBLET_OF_EONOTHEM -> Icons.Default.WineBar
        ArtifactSlot.CIRCLET_OF_LOGOS -> Icons.Default.School
    }

    Column {
        Text(
            text = "Слот",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ArtifactSlot.entries.forEach { slot ->
                val isSelected = slot in selectedArtifactSlots

                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else
                    MaterialTheme.colorScheme.surfaceVariant
                val iconColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else
                    MaterialTheme.colorScheme.onSurfaceVariant

                Surface(
                    shape = CircleShape,
                    color = backgroundColor
                ) {
                    IconButton(onClick = {onArtifactSlotClicked(slot)}) {
                        Icon(
                            imageVector = getIconForSlot(slot),
                            contentDescription = slot.displayName,
                            tint = iconColor
                        )
                    }
                }
            }
        }
    }
}