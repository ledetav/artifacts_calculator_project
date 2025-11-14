package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.presentation.ui.common.components.SimpleDropdown
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName

@Composable
fun ArtifactMainStatFilter(
    selectedArtifactMainStat: StatType?,
    onArtifactMainStatSelected: (StatType) -> Unit,
    onClearSelectedArtifactMainStat: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.filter_artifact_main_stat),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SimpleDropdown(
            items = StatType.entries,
            selectedItem = selectedArtifactMainStat,
            onItemSelected = onArtifactMainStatSelected,
            onClearSelection = onClearSelectedArtifactMainStat,
            placeholderText = stringResource(R.string.filter_artifact_main_stat_choose),
            itemText = @Composable { it.toDisplayName() }
        )
    }
}