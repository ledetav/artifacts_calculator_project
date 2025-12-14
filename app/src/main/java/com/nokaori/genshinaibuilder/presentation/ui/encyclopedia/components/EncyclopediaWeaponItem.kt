package com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Weapon

@Composable
fun EncyclopediaWeaponItem(
    weapon: Weapon,
    onClick: () -> Unit
) {
    val bgColor = when (weapon.rarity) {
        Rarity.FIVE_STARS -> Color(0xFFEBC45D) // Gold
        Rarity.FOUR_STARS -> Color(0xFF9E86C6) // Purple
        else -> Color(0xFF708CA2) // Blue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .aspectRatio(0.8f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor.copy(alpha = 0.3f))
            )

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(weapon.iconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = weapon.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = weapon.name,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp)
            )
        }
    }
}