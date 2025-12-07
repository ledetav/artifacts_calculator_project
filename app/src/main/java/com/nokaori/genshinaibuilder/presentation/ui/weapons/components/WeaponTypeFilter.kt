package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import coil3.compose.rememberAsyncImagePainter
import com.nokaori.genshinaibuilder.presentation.ui.common.components.IconToggleButton
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MultiSelectToggleButtonGroup
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName
import com.nokaori.genshinaibuilder.presentation.ui.common.Orientation
import com.nokaori.genshinaibuilder.presentation.util.YattaAssets

@Composable
fun WeaponTypeFilter(
    selectedWeaponTypes: Set<WeaponType>,
    onWeaponTypeSelected: (WeaponType) -> Unit,
    orientation: Orientation = Orientation.HORIZONTAL
) {
    MultiSelectToggleButtonGroup(
        title = stringResource(R.string.filter_weapon_type),
        items = WeaponType.entries.toList(),
        selectedItems = selectedWeaponTypes,
        onItemClick = onWeaponTypeSelected,
        orientation = orientation
    ) { weaponType, isSelected ->

        val iconUrl = YattaAssets.getWeaponTypeIconUrl(weaponType)
        val painter = rememberAsyncImagePainter(iconUrl)

        IconToggleButton(
            onClick = { onWeaponTypeSelected(weaponType) },
            isSelected = isSelected,
            painter = painter,
            contentDescription = weaponType.toDisplayName()
        )
    }
}
