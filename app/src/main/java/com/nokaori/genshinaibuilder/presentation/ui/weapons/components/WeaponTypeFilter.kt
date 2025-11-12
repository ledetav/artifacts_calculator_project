package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.R
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
        title = stringResource(R.string.filter_weapon_type),
        items = WeaponType.entries.toList(),
        selectedItems = selectedWeaponTypes,
        onItemClick = onWeaponTypeSelected,
        modifier = modifier
    ) { weaponType, isSelected ->
        Text(
            text = weaponType.toDisplayName()
        )
    }
}
