package com.nokaori.genshinaibuilder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import com.nokaori.genshinaibuilder.data.Artifact
import com.nokaori.genshinaibuilder.data.ArtifactStat
import com.nokaori.genshinaibuilder.data.StatValue
import com.nokaori.genshinaibuilder.viewmodel.ArtifactViewModel

@Composable
fun ArtifactScreen(artifactViewModel: ArtifactViewModel = viewModel()) {
    val searchQuery by artifactViewModel.searchQuery.collectAsState()
    val searchedArtifacts by artifactViewModel.searchedArtifacts.collectAsState()

    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Артефакты",
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(onClick = { artifactViewModel.addDefaultaArtifact() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить артефакт"
                )
            }
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newText ->
                artifactViewModel.onSearchQueryChange(newText)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Поиск по артефактам") },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        // Заглушка.
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Фильтр артефактов"
                    )
                }
            }
        )

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
                text = artifact.artifactName,
                style = MaterialTheme.typography.titleLarge
            )
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
                text = artifact.slot.diasplayName,
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