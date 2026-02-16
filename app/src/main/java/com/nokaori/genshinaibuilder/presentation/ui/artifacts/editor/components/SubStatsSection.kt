package com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data.SubStatState
import com.nokaori.genshinaibuilder.presentation.ui.common.components.SimpleDropdown
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName
import com.nokaori.genshinaibuilder.presentation.ui.theme.getRarityColor

@Composable
fun SubStatsSection(
    subStats: List<SubStatState>,
    artifactRarity: Rarity,
    canAddMore: Boolean,
    enabled: Boolean,
    onAddSubStat: () -> Unit,
    onRemoveSubStat: (Long) -> Unit,
    onTypeChanged: (Long, StatType) -> Unit,
    onRollAdded: (Long, Float) -> Unit,
    onRollRemoved: (Long, Int) -> Unit,
    onManualInput: (Long, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
            .alpha(if (enabled) 1f else 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Substats", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (canAddMore && enabled) {
                FilledTonalIconButton(onClick = onAddSubStat, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (subStats.isEmpty()) {
            Text("Add substats...", modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
        } else {
            subStats.forEachIndexed { index, subStat ->
                SubStatItem(
                    subStat = subStat,
                    artifactRarity = artifactRarity,
                    onTypeChanged = { type -> onTypeChanged(subStat.id, type) },
                    onRollAdded = { value -> onRollAdded(subStat.id, value) },
                    onRollRemoved = { idx -> onRollRemoved(subStat.id, idx) },
                    onRemove = { onRemoveSubStat(subStat.id) },
                    onManualInput = { value -> onManualInput(subStat.id, value) },
                    enabled = enabled
                )
                if (index < subStats.lastIndex) Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubStatItem(
    subStat: SubStatState,
    artifactRarity: Rarity,
    onTypeChanged: (StatType) -> Unit,
    onRollAdded: (Float) -> Unit,
    onRollRemoved: (Int) -> Unit,
    onRemove: () -> Unit,
    onManualInput: (String) -> Unit,
    enabled: Boolean
) {
    val stripColor = getRarityColor(artifactRarity)
    val isPercentage = subStat.type?.isPercentage == true

    fun formatVal(v: Float): String = if (isPercentage) "%.1f".format(v * 100) else "%.0f".format(v)

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ManualValueDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
                onManualInput(it)
                showDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier.width(32.dp).fillMaxHeight().background(stripColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "${subStat.rollCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        var expanded by remember { mutableStateOf(false) }
                        SimpleDropdown(
                            items = subStat.availableTypes,
                            selectedItem = subStat.type,
                            onItemSelected = onTypeChanged,
                            onClearSelection = null,
                            placeholderText = "Select",
                            itemText = { it.toDisplayName() },
                            isExpanded = expanded,
                            onExpandedChange = { expanded = it },
                            modifier = Modifier.height(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val totalText = if (subStat.type == null) "—" else {
                        val suffix = if (isPercentage) "%" else ""
                        "${formatVal(subStat.value)}$suffix"
                    }

                    Text(
                        text = totalText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable(enabled = enabled) { showDialog = true }
                    )

                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.outline)
                    }
                }

                if (subStat.rollHistory.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        subStat.rollHistory.forEachIndexed { idx, rollVal ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.clickable(enabled = enabled) { onRollRemoved(idx) }
                            ) {
                                Text(text = formatVal(rollVal), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = subStat.type != null && subStat.rollCount < 6) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val tierColors = listOf(getRarityColor(Rarity.TWO_STARS), getRarityColor(Rarity.THREE_STARS), getRarityColor(Rarity.FOUR_STARS), getRarityColor(Rarity.FIVE_STARS))
                        subStat.tierValues.forEachIndexed { index, tierVal ->
                            val nextValue = subStat.value + tierVal
                            val btnColor = tierColors.getOrElse(index) { Color.Gray }
                            Button(
                                onClick = { onRollAdded(tierVal) },
                                enabled = enabled,
                                colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                                contentPadding = PaddingValues(0.dp),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1f).height(32.dp)
                            ) {
                                Text(text = formatVal(nextValue), style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManualValueDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Value") },
        text = {
            TextField(
                value = text,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) text = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
            )
        },
        confirmButton = { TextButton(onClick = { onConfirm(text) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}