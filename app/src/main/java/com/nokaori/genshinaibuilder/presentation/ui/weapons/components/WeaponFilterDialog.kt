package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    areWeaponFiltersChanged: Boolean,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onWeaponTypeSelected: (WeaponType) -> Unit,
    onWeaponLevelRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onLevelManualInput: (String, String) -> Unit,
    onMainStatSelected: (com.nokaori.genshinaibuilder.domain.model.StatType) -> Unit,
    onClearMainStat: () -> Unit
) {
    BaseDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.filter_dialog_title),
        content = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                WeaponTypeFilter(
                    selectedWeaponTypes = weaponFilterState.selectedWeaponTypes,
                    onWeaponTypeSelected = onWeaponTypeSelected
                )

                Spacer(modifier = Modifier.height(16.dp))

                WeaponLevelFilter(
                    weaponLevelRange = weaponFilterState.levelRange,
                    onWeaponLevelRangeChanged = onWeaponLevelRangeChanged,
                    onLevelManualInput = onLevelManualInput
                )

                Spacer(modifier = Modifier.height(16.dp))

                WeaponMainStatFilter(
                    selectedMainStat = weaponFilterState.selectedMainStat,
                    onMainStatSelected = onMainStatSelected,
                    onClearSelection = onClearMainStat
                )
            }
        },
        actions = {
            TextButton(onClick = onReset) {
                Text(stringResource(R.string.reset))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onApply,
                enabled = areWeaponFiltersChanged
            ) {
                Text(stringResource(R.string.apply))
            }
        }
    )
}
