package com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.presentation.ui.common.components.LevelSlider
import com.nokaori.genshinaibuilder.presentation.ui.common.components.SimpleDropdown
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName

@Composable
fun MainStatSection(
    mainStatType: StatType?,
    mainStatValue: Float,
    availableStats: List<StatType>,
    level: Int,
    maxLevel: Int,
    onStatSelected: (StatType) -> Unit,
    onLevelChanged: (Int) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
            .alpha(if (enabled) 1f else 0.4f)
    ) {
        Text(
            text = stringResource(R.string.filter_artifact_main_stat),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                if (!enabled) {
                    Text("—", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else if (availableStats.size > 1) {
                    var expanded by remember { mutableStateOf(false) }
                    SimpleDropdown(
                        items = availableStats,
                        selectedItem = mainStatType,
                        onItemSelected = onStatSelected,
                        onClearSelection = null,
                        placeholderText = "Select",
                        itemText = { it.toDisplayName() },
                        isExpanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.height(48.dp)
                    )
                } else {
                    Text(text = mainStatType?.toDisplayName() ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            val valueText = if (!enabled) "???" else if (mainStatType?.isPercentage == true) "%.1f%%".format(mainStatValue * 100) else "%.0f".format(mainStatValue)

            Text(
                text = valueText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (enabled) {
            LevelSlider(
                value = level,
                maxLevel = maxLevel,
                onValueChange = onLevelChanged
            )
        } else {
            Text("Lv. ???", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        }
    }
}