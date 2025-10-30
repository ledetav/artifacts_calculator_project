package com.nokaori.genshinaibuilder.ui.artifacts.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.domain.model.StatType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactMainStatFilter(
    selectedArtifactMainStat: StatType?,
    onArtifactMainStatSelected: (StatType) -> Unit,
    onClearSelectedArtifactMainStat: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val allArtifactStats = remember { StatType.entries.toTypedArray() }

    Column {
        Text(
            text = "Главный стат",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { isExpanded = true}
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedArtifactMainStat?.displayName ?: "Выбрать главный стат",
                    color = if (selectedArtifactMainStat != null) MaterialTheme.colorScheme.onSurface else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (selectedArtifactMainStat != null) {
                    IconButton(
                        onClick = onClearSelectedArtifactMainStat,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить выбор"
                        )
                    }
                } else {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else
                            Icons.Default.ArrowDropDown,
                        contentDescription = "открыть список"
                    )
                }
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                allArtifactStats.forEach { artifactStat ->
                    DropdownMenuItem(
                        text = { Text(text = artifactStat.displayName) },
                        onClick = {
                            onArtifactMainStatSelected(artifactStat)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}