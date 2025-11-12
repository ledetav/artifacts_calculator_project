package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MultiSelectToggleButtonGroup
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName

@Composable
fun WeaponTypeFilter(
    selectedWeaponTypes: Set<WeaponType>,
    onWeaponTypeSelected: (WeaponType) -> Unit,
    modifier: Modifier = Modifier
) {
    MultiSelectToggleButtonGroup(
        options = WeaponType.entries.toSet(),
        selectedOptions = selectedWeaponTypes,
        onOptionSelected = onWeaponTypeSelected,
        modifier = modifier
    ) { weaponType ->
        weaponType.toDisplayName()
    }
}
