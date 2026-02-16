package com.nokaori.genshinaibuilder.presentation.ui.encyclopedia.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.domain.model.Weapon
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

@Composable
fun EncyclopediaWeaponsTab(
    weapons: LazyPagingItems<Weapon>,
    onWeaponClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 85.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = weapons.itemCount,
            key = weapons.itemKey { it.id },
            contentType = weapons.itemContentType { "weapon" }
        ) { index ->
            val weapon = weapons[index]
            
            if (weapon != null) {
                EncyclopediaWeaponItem(
                    weapon = weapon,
                    onClick = { onWeaponClick(weapon.id) }
                )
            } else {
                // Если элемент еще грузится (placeholder), можно показать серый квадрат (TODO)
            }
        }
    }
}