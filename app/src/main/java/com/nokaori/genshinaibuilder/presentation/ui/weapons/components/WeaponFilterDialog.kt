package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseDialog
import com.nokaori.genshinaibuilder.presentation.ui.weapons.data.WeaponFilterState

@Composable
fun WeaponFilterDialog(
    weaponFilterState: WeaponFilterState,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onWeaponTypeSelected: (WeaponType) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDialog(
        title = stringResource(R.string.filter_dialog_title),
        onDismiss = onDismiss,
        onApply = onApply,
        onReset = onReset,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.filter_weapon_type))
            Spacer(modifier = Modifier.height(8.dp))
            WeaponTypeFilter(
                selectedWeaponTypes = weaponFilterState.selectedWeaponTypes,
                onWeaponTypeSelected = onWeaponTypeSelected
            )
        }
    }
}