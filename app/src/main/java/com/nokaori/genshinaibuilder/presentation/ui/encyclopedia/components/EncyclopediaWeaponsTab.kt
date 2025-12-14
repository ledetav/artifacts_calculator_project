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

@Composable
fun EncyclopediaWeaponsTab(weapons: List<Weapon>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 85.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = weapons,
            key = { it.id },
            contentType = { "weapon" }
        ) { weapon ->
            EncyclopediaWeaponItem(
                weapon = weapon,
                onClick = { /* TODO */ }
            )
        }
    }
}