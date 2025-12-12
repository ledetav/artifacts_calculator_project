package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.ui.common.components.RangeSelector

@Composable
fun ArtifactLevelFilter(
    artifactLevelRange: ClosedFloatingPointRange<Float>,
    onArtifactLevelRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onLevelManualInput: (String, String) -> Unit
) {
    RangeSelector(
        title = stringResource(R.string.filter_level),
        range = artifactLevelRange,
        valueRange = 0f..20f,
        keyValues = listOf(0, 4, 8, 12, 16, 20),
        fromLabel = stringResource(R.string.filter_level_from),
        toLabel = stringResource(R.string.filter_level_to),
        onRangeChanged = onArtifactLevelRangeChanged,
        onManualInput = onLevelManualInput
    )
}