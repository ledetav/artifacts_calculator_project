package com.nokaori.genshinaibuilder.presentation.ui.characters.details.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.nokaori.genshinaibuilder.domain.model.CharacterConstellation
import com.nokaori.genshinaibuilder.domain.model.CharacterTalent
import com.nokaori.genshinaibuilder.domain.model.TalentType
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R

@Composable
fun TalentsList(
    talents: List<CharacterTalent>,
    userLevels: List<Int>?,
    elementColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        talents.forEachIndexed { index, talent ->
            val level = userLevels?.getOrNull(index) ?: 1

            TalentItem(
                talent = talent,
                isLocked = false,
                level = level,
                elementColor = elementColor
            )
        }
    }
}

@Composable
fun TalentItem(
    talent: CharacterTalent,
    isLocked: Boolean,
    level: Int,
    elementColor: Color
) {
    val alpha = if (isLocked) 0.5f else 1f

    val showLevel = when(talent.type) {
        TalentType.NORMAL_ATTACK,
        TalentType.ELEMENTAL_SKILL,
        TalentType.ELEMENTAL_BURST -> true
        else -> false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .alpha(alpha),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(talent.iconUrl).build(),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    colorFilter = ColorFilter.tint(elementColor)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = talent.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = elementColor
                    )

                    if (showLevel) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = elementColor,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.talent_level_fmt, level),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.talent_passive),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = elementColor.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = talent.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface, 
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ConstellationsList(
    constellations: List<CharacterConstellation>,
    unlockedCount: Int,
    elementColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        constellations.forEach { cons ->
            val isUnlocked = cons.order <= unlockedCount
            ConstellationItem(cons, isUnlocked, elementColor)
        }
    }
}

@Composable
fun ConstellationItem(
    cons: CharacterConstellation,
    isUnlocked: Boolean,
    elementColor: Color
) {
    val alpha = if (isUnlocked) 1f else 0.4f

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).alpha(alpha),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(cons.iconUrl).build(),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(elementColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = cons.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = cons.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if(isUnlocked) 10 else 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}