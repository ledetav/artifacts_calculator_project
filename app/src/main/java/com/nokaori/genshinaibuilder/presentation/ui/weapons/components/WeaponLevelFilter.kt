package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.ui.common.components.RangeSelector

@Composable
fun WeaponLevelFilter(
    weaponLevelRange: ClosedFloatingPointRange<Float>,
    onWeaponLevelRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onLevelManualInput: (String, String) -> Unit
) {
    RangeSelector(
        title = stringResource(R.string.filter_level),
        range = weaponLevelRange,
        valueRange = 0f..90f,
        keyValues = listOf(0, 20, 40, 50, 70, 80, 90),
        fromLabel = stringResource(R.string.filter_level_from),
        toLabel = stringResource(R.string.filter_level_to),
        onRangeChanged = onWeaponLevelRangeChanged,
        onManualInput = onLevelManualInput
    )
}
