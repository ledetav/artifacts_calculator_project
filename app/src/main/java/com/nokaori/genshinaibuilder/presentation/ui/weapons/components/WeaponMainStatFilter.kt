package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

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
fun WeaponMainStatFilter(
    selectedMainStat: StatType?,
    onMainStatSelected: (StatType) -> Unit,
    onClearSelection: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.filter_weapon_main_stat),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SimpleDropdown(
            items = StatType.entries,
            selectedItem = selectedMainStat,
            onItemSelected = onMainStatSelected,
            onClearSelection = onClearSelection,
            placeholderText = stringResource(R.string.filter_weapon_main_stat_choose),
            itemText = @Composable { it.toDisplayName() }
        )
    }
}
