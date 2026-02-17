// ledetav/artifacts_calculator_project/artifacts_calculator_project-feature-artifact-management-rework/app/src/main/java/com/nokaori/genshinaibuilder/presentation/ui/artifacts/components/ArtifactItem.kt

package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.presentation.ui.theme.getRarityBackgroundColor

@Composable
fun ArtifactItem(
    artifact: Artifact,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .aspectRatio(0.7f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = getRarityBackgroundColor(artifact.rarity))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(artifact.iconUrl)
                        .crossfade(false)
                        .build(),
                    contentDescription = artifact.artifactName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(1f)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f),
                                Color.Black.copy(alpha = 0.95f)
                            ),
                            startY = 0f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "+${artifact.level}",
                        color = Color(0xFFFFD700),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "⭐".repeat(artifact.rarity.stars),
                            color = Color(0xFFFFD700),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatIcon(
                                statType = artifact.mainStat.type,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatStatValue(artifact.mainStat),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                val chunks = artifact.subStats.chunked(2)
                chunks.forEach { rowStats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowStats.forEach { stat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                            ) {
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
                        if (rowStats.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun formatStatValue(stat: Stat): String {
    val rawValue = when (val statValue = stat.value) {
        is StatValue.IntValue -> statValue.value.toDouble()
        is StatValue.DoubleValue -> statValue.value
    }

    val adjustedValue = if (stat.type.isPercentage) rawValue * 100 else rawValue
    
    val rounded = Math.round(adjustedValue * 10.0) / 10.0
    val valueString = if (rounded % 1 == 0.0) rounded.toInt().toString() else rounded.toString()

    return if (stat.type.isPercentage) "$valueString%" else valueString
}