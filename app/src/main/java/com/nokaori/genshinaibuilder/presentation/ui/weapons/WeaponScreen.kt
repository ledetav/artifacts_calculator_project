package com.nokaori.genshinaibuilder.presentation.ui.weapons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.ui.weapons.components.UserWeaponItem
import com.nokaori.genshinaibuilder.presentation.viewmodel.WeaponViewModel
import com.nokaori.genshinaibuilder.presentation.ui.weapons.components.WeaponFilterDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeaponScreen(
    modifier: Modifier = Modifier,
    weaponViewModel: WeaponViewModel
) {
    val searchQuery by weaponViewModel.searchQuery.collectAsState()
    val searchedWeapons by weaponViewModel.searchedWeapons.collectAsState()
    val weaponFilterState by weaponViewModel.weaponFilterState.collectAsState()

    val isFilterDialogShown = false // TODO: Implement state for showing filter dialog

    if (isFilterDialogShown) {
        WeaponFilterDialog(
            weaponFilterState = weaponFilterState,
            onDismiss = { /* TODO: Implement dismiss */ },
            onApply = { /* TODO: Implement apply */ },
            onReset = { /* TODO: Implement reset */ },
            onWeaponTypeSelected = weaponViewModel::onWeaponTypeSelected
        )
    }

    Column(modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newText ->
                weaponViewModel.onSearchQueryChange(newText)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            label = { Text(stringResource(R.string.weapon_search_placeholder)) },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = { /* TODO: Implement filter functionality */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filter_dialog_title)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(searchedWeapons) { weapon ->
                UserWeaponItem(userWeapon = weapon)
            }
        }
    }
}