package com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.WeaponRefinement
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseItemCard
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName
import com.nokaori.genshinaibuilder.presentation.ui.theme.getRarityColor
import com.nokaori.genshinaibuilder.presentation.viewmodel.WeaponDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeaponDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: WeaponDetailsViewModel = hiltViewModel()
) {
    val weapon by viewModel.weapon.collectAsStateWithLifecycle()

    if (weapon == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = {
                    Text(
                        text = weapon?.name ?: "",
                        modifier = Modifier.offset(x = (-4).dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
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
            WeaponStatsSection(weapon!!)

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            weapon?.refinement?.let { refinement ->
                RefinementsSection(refinement)
            }
        }
    }
}

@Composable
fun WeaponStatsSection(weapon: Weapon) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseItemCard(
                name = "",
                iconUrl = weapon.iconUrl,
                rarity = weapon.rarity,
                onClick = {},
                aspectRatio = 1f,
                bottomContent = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "⭐".repeat(weapon.rarity.stars),
                style = MaterialTheme.typography.bodyMedium,
                color = getRarityColor(weapon.rarity)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = weapon.type.toDisplayName(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Column(modifier = Modifier.weight(1.5f)) {
            Text(
                text = stringResource(R.string.char_section_attributes),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StatRow(stringResource(R.string.stat_label_level), "1/90")
            StatRow(stringResource(R.string.stat_type_atk), weapon.baseAttackLvl1.toString())

            weapon.mainStat?.let { stat ->
                val valueStr = when(val v = stat.value) {
                    is StatValue.IntValue -> v.value.toString()
                    is StatValue.DoubleValue -> "%.1f%%".format(v.value)
                }
                StatRow(stat.type.toDisplayName(showPercentSign = false), valueStr)
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun RefinementsSection(refinement: WeaponRefinement) {
    Text(
        text = refinement.passiveName,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(16.dp))

    refinement.descriptions.forEachIndexed { index, desc ->
        RefinementItem(rank = index + 1, description = desc)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun RefinementItem(rank: Int, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "R$rank",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.offset(y = (-2).dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}