package com.nokaori.genshinaibuilder.presentation.ui.characters.details.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseItemCard
import com.nokaori.genshinaibuilder.presentation.ui.theme.getElementColor
import com.nokaori.genshinaibuilder.presentation.ui.theme.getRarityColor
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R

@Composable
fun CharacterInfoSection(
    character: Character,
    userCharacter: UserCharacter?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp, 16.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseItemCard(
                name = "",
                iconUrl = character.iconUrl,
                rarity = character.rarity,
                onClick = {},
                aspectRatio = 1f,
                backgroundColor = getElementColor(character.element).copy(alpha = 0.5f),
                bottomContent = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "⭐".repeat(character.rarity.stars),
                style = MaterialTheme.typography.bodyMedium,
                color = getRarityColor(character.rarity)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1.5f)) {
            Text(
                text = stringResource(R.string.char_section_attributes),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val level = userCharacter?.level ?: 1

            // TODO: В будущем подключить реальный калькулятор статов
            StatRow(stringResource(R.string.stat_label_level), "$level/90")
            StatRow(stringResource(R.string.stat_type_hp), "???")
            StatRow(stringResource(R.string.stat_type_atk), "???")
            StatRow(stringResource(R.string.stat_type_def), "???")
            StatRow(stringResource(R.string.stat_type_elemental_mastery), "0")
            StatRow(stringResource(R.string.stat_type_crit_rate), "5.0%")
            StatRow(stringResource(R.string.stat_type_crit_dmg), "50.0%")
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}