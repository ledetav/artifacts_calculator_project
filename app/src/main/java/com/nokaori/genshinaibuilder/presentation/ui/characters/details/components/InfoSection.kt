package com.nokaori.genshinaibuilder.presentation.ui.characters.details.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseItemCard
import com.nokaori.genshinaibuilder.presentation.ui.theme.getElementColor

@Composable
fun CharacterInfoSection(
    character: Character,
    userCharacter: UserCharacter?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BaseItemCard(
                name = "",
                iconUrl = character.iconUrl,
                rarity = character.rarity,
                onClick = {},
                aspectRatio = 1f,
                backgroundColor = getElementColor(character.element).copy(alpha = 0.5f)
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
                color = com.nokaori.genshinaibuilder.presentation.ui.theme.getRarityColor(character.rarity)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Правая часть: Статы
        Column(modifier = Modifier.weight(1.5f)) {
            Text(
                text = "Характеристики",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Если есть userCharacter, берем его статы (пока заглушка, т.к. нужно считать статы)
            // Если нет, берем базовые 1 уровня
            val level = userCharacter?.level ?: 1
            val hp = if(userCharacter != null) "???" else "Base HP" // TODO: Подключить калькулятор
            
            StatRow("Уровень", "$level/90")
            StatRow("HP", hp) // Заглушка
            StatRow("ATK", "???")
            StatRow("DEF", "???")
            StatRow("МС", "0")
            StatRow("Крит. шанс", "5.0%")
            StatRow("Крит. урон", "50.0%")
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