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
import com.nokaori.genshinaibuilder.R
import androidx.compose.ui.res.stringResource

@Composable
fun EquipmentSection(
    equippedWeapon: UserWeapon?,
    onAddWeaponClick: () -> Unit,
    onAddArtifactClick: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.char_section_equipment),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
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
            .border(1.dp, color, RoundedCornerShape(8.dp)), 
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.cd_add_item),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
    }
}