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
    orientation: Orientation = Orientation.HORIZONTAL
) {
    fun getIconForType(type: WeaponType) = when (type) {
        WeaponType.BOW -> Icons.Default.NorthEast
        WeaponType.CLAYMORE -> Icons.Default.SportsKabaddi
        WeaponType.POLEARM -> Icons.Default.SportsKabaddi
        WeaponType.SWORD -> Icons.Default.Remove
        WeaponType.CATALYST -> Icons.Default.Book
    }
    
    MultiSelectToggleButtonGroup(
        title = stringResource(R.string.filter_weapon_type),
        items = WeaponType.entries.toList(),
        selectedItems = selectedWeaponTypes,
        onItemClick = onWeaponTypeSelected,
        modifier = modifier
    ) { weaponType, isSelected ->
        IconToggleButton(
            onClick = { onWeaponTypeSelected(weaponType) },
            isSelected = isSelected,
            icon = getIconForType(weaponType),
            contentDescription = weaponType.toDisplayName()
        )
    }
}
