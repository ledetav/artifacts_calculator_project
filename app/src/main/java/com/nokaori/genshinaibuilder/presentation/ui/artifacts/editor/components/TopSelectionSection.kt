package com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.presentation.ui.theme.getRarityBackgroundColor
import com.nokaori.genshinaibuilder.presentation.util.YattaAssets
import coil3.compose.rememberAsyncImagePainter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopSelectionSection(
    selectedSet: ArtifactSet?,
    availableRarities: List<Rarity>,
    selectedRarity: Rarity,
    selectedSlot: ArtifactSlot,
    onSetClick: () -> Unit,
    onRaritySelect: (Rarity) -> Unit,
    onSlotSelect: (ArtifactSlot) -> Unit,
    currentIconUrl: String?,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        SetSelectorBox(
            selectedSet = selectedSet,
            selectedRarity = selectedRarity,
            iconUrl = currentIconUrl,
            onClick = onSetClick,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxHeight()
                .alpha(if (enabled) 1f else 0.4f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                maxItemsInEachRow = 3
            ) {
                val listToShow = availableRarities.ifEmpty {
                    listOf(
                        Rarity.THREE_STARS,
                        Rarity.FOUR_STARS,
                        Rarity.FIVE_STARS
                    )
                }
                listToShow.forEach { rarity ->
                    RarityButton(
                        rarity = rarity,
                        isSelected = rarity == selectedRarity,
                        onClick = { if (enabled) onRaritySelect(rarity) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ArtifactSlot.entries.forEach { slot ->
                    SlotButton(
                        slot = slot,
                        isSelected = slot == selectedSlot,
                        onClick = { if (enabled) onSlotSelect(slot) }
                    )
                }
            }
        }
    }
}

@Composable
fun SetSelectorBox(
    selectedSet: ArtifactSet?,
    selectedRarity: Rarity,
    iconUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val stroke =
        Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f))
    val dashColor = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick)
            .then(if (selectedSet == null) Modifier.drawBehind {
                drawRoundRect(
                    color = dashColor,
                    style = stroke,
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            } else Modifier)
            .background(if (selectedSet != null) getRarityBackgroundColor(selectedRarity) else Color.Transparent)
    ) {
        if (selectedSet != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconUrl ?: selectedSet.iconUrl).build(),
                contentDescription = selectedSet.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(4.dp)
            ) {
                Text(
                    text = selectedSet.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.filter_artifact_set_choose),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RarityButton(
    rarity: Rarity,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor =
        if (isSelected) getRarityBackgroundColor(rarity).copy(alpha = 1f) else MaterialTheme.colorScheme.surfaceContainerHigh
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "${rarity.stars}★",
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
        }
    }
}

@Composable
fun SlotButton(slot: ArtifactSlot, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor =
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = Modifier.size(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
            AsyncImage(
                model = YattaAssets.getArtifactSlotIconUrl(slot),
                contentDescription = slot.name,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}