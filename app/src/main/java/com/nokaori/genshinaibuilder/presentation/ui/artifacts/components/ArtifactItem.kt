package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseItemCard

@Composable
fun ArtifactItem(
    artifact: Artifact,
    onClick: () -> Unit = {}
) {
    BaseItemCard(
        name = artifact.artifactName,
        iconUrl = artifact.iconUrl,
        rarity = artifact.rarity,
        onClick = onClick,
        aspectRatio = 0.7f,
        bottomContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "+${artifact.level}",
                    color = Color(0xFFFFD700),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatIcon(
                        statType = artifact.mainStat.type,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatStatValue(artifact.mainStat),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val chunks = artifact.subStats.chunked(2)
                chunks.forEach { rowStats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowStats.forEach { stat ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatIcon(
                                    statType = stat.type,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = formatStatValue(stat),
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    )
}

@Composable
private fun formatStatValue(stat: Stat): String {
    val valueString = when (val statValue = stat.value) {
        is StatValue.IntValue -> statValue.value.toString()
        is StatValue.DoubleValue -> {
            val rounded = Math.round(statValue.value * 10.0) / 10.0
            if (rounded % 1 == 0.0) rounded.toInt().toString() else rounded.toString()
        }
    }

    return if (stat.type.isPercentage) {
        "$valueString%"
    } else {
        valueString
    }
}