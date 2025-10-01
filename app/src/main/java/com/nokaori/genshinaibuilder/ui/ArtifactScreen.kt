package com.nokaori.genshinaibuilder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.OutlinedTextField
import com.nokaori.genshinaibuilder.data.Artifact
import com.nokaori.genshinaibuilder.data.ArtifactStat
import com.nokaori.genshinaibuilder.data.StatValue
import com.nokaori.genshinaibuilder.viewmodel.ArtifactViewModel

@Composable
fun ArtifactScreen(artifactViewModel: ArtifactViewModel = viewModel()) {
    val searchQuery by artifactViewModel.searchQuery.collectAsState()
    val searchedArtifacts by artifactViewModel.searchedArtifacts.collectAsState()

    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Артефакты",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newText ->
                artifactViewModel.onSearchQueryChange(newText)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Поиск по артефактам") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                artifactViewModel.addDefaultaArtifact()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Добавить артефакт",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searchedArtifacts) {
                artifact -> ArtifactItem(artifact = artifact)
            }
        }
    }
}

@Composable
fun ArtifactItem(artifact: Artifact){
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${artifact.setName} (+${artifact.level})",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "⭐".repeat(artifact.rarity.stars),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = artifact.slot.name,
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
        val cleanName = stat.type.displayName.replace(" %", "")
        "$cleanName ${valueString}%"
    } else {
        "${stat.type.displayName} $valueString"
    }
}