package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nokaori.genshinaibuilder.presentation.ui.common.components.IconToggleButton
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.presentation.ui.common.components.MultiSelectToggleButtonGroup
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName
import com.nokaori.genshinaibuilder.presentation.ui.common.Orientation

@Composable
fun WeaponTypeFilter(
    selectedWeaponTypes: Set<WeaponType>,
    onWeaponTypeSelected: (WeaponType) -> Unit,
    orientation: Orientation = Orientation.HORIZONTAL
) {
    fun getIconForType(type: WeaponType) = when (type) {
        WeaponType.BOW -> Icons.Filled.NorthEast
        WeaponType.CLAYMORE -> Icons.Filled.SportsKabaddi
        WeaponType.POLEARM -> Icons.Filled.SportsKabaddi
        WeaponType.SWORD -> Icons.Filled.Remove
        WeaponType.CATALYST -> Icons.Filled.Book
    }
    
    MultiSelectToggleButtonGroup(
        title = stringResource(R.string.filter_weapon_type),
        items = WeaponType.entries.toList(),
        selectedItems = selectedWeaponTypes,
        onItemClick = onWeaponTypeSelected,
        orientation = orientation
    ) { weaponType, isSelected ->
        IconToggleButton(
            onClick = { onWeaponTypeSelected(weaponType) },
            isSelected = isSelected,
            icon = getIconForType(weaponType),
            contentDescription = weaponType.toDisplayName()
        )
    }
}
