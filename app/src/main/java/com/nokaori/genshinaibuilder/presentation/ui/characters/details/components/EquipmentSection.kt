package com.nokaori.genshinaibuilder.presentation.ui.characters.details.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseItemCard

@Composable
fun EquipmentSection(
    equippedWeapon: UserWeapon?,
    onAddWeaponClick: () -> Unit,
    onAddArtifactClick: (Int) -> Unit // slot index
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Экипировка",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            // Оружие (большой слот)
            Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                if (equippedWeapon != null) {
                    BaseItemCard(
                        name = equippedWeapon.weapon.name,
                        iconUrl = equippedWeapon.weapon.iconUrl,
                        rarity = equippedWeapon.weapon.rarity,
                        onClick = { /* Open Weapon Details */ },
                        aspectRatio = 1f
                    )
                } else {
                    EmptySlot(onClick = onAddWeaponClick)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Артефакты (сетка 2x3, но у нас 5 слотов, так что просто Row с переносом или LazyGrid)
            // Для простоты сделаем Row со скроллом или просто 5 мелких слотов
            Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(5) { index ->
                    // TODO: Check equipped artifacts for slot $index
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                         EmptySlot(onClick = { onAddArtifactClick(index) }, iconSize = 16.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptySlot(onClick: () -> Unit, iconSize: androidx.compose.ui.unit.Dp = 24.dp) {
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    val color = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(1.dp, color, RoundedCornerShape(8.dp)), // Fallback border
        contentAlignment = Alignment.Center
    ) {
        // Кастомная отрисовка пунктира (опционально, если border выше не устраивает)
        // Canvas(modifier = Modifier.fillMaxSize()) { drawRoundRect(color = color, style = stroke, cornerRadius = CornerRadius(8.dp.toPx())) }
        
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
    }
}