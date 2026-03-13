package com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.ArtifactPiece
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseItemCard
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName
import com.nokaori.genshinaibuilder.presentation.ui.theme.getRarityColor
import com.nokaori.genshinaibuilder.presentation.viewmodel.ArtifactSetDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactSetDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: ArtifactSetDetailsViewModel = hiltViewModel()
) {
    val details by viewModel.details.collectAsStateWithLifecycle()

    if (details == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val set = details!!

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = {
                    Text(
                        text = set.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.offset(x = (-4).dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.character_detail_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SetHeaderSection(set)

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.nav_artifacts), // "Артефакты"
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            set.pieces.forEach { piece ->
                ArtifactPieceItem(piece)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SetHeaderSection(set: ArtifactSet) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseItemCard(
                name = "",
                iconUrl = set.iconUrl,
                onClick = {},
                aspectRatio = 1f,
                rarity = set.rarities.maxByOrNull { it.stars },
                bottomContent = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            val starsText = set.rarities
                .sortedBy { it.stars }
                .joinToString(" / ") { "${it.stars}⭐" }

            Text(
                text = starsText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier
            .weight(2f)
            .align(Alignment.CenterVertically)
        ) {
            if (set.bonus2pc.isNotBlank()) {
                BonusItem(count = 2, desc = set.bonus2pc)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (set.bonus4pc.isNotBlank()) {
                BonusItem(count = 4, desc = set.bonus4pc)
            }
        }
    }
}

@Composable
fun BonusItem(count: Int, desc: String) {
    Column {
        Text(
            text = stringResource(R.string.artifact_section_pieces, count),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ArtifactPieceItem(piece: ArtifactPiece) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(piece.iconUrl).build(),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = piece.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = piece.slot.toDisplayName(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}