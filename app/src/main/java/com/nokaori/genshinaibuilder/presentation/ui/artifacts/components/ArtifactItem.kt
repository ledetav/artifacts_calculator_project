package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactStat
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName
import com.nokaori.genshinaibuilder.R
import androidx.compose.ui.res.stringResource

@Composable
fun ArtifactItem(artifact: Artifact){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = artifact.artifactName,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(
                    id = R.string.artifact_set_and_level,
                    artifact.setName,
                    artifact.level
                ),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "⭐".repeat(artifact.rarity.stars),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = artifact.slot.toDisplayName(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatStat(artifact.mainStat),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun formatStat(stat: ArtifactStat): String {
    val valueString = when (val statValue = stat.value) {
        is StatValue.IntValue -> statValue.value.toString()
        is StatValue.DoubleValue -> statValue.value.toString()
    }

    return if (stat.type.isPercentage) {
        val cleanName = stat.type.toDisplayName().replace(" %", "")
        "$cleanName ${valueString}%"
    } else {
        "${stat.type.toDisplayName()} $valueString"
    }
}