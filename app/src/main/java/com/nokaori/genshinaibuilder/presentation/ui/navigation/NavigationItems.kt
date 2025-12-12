package com.nokaori.genshinaibuilder.presentation.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector
import com.nokaori.genshinaibuilder.R

sealed class NavigationItem(
    val route: String,
    @StringRes val titleResId: Int,
    val icon: ImageVector
) {
    object Characters : NavigationItem("encyclopedia/characters", R.string.nav_characters, Icons.Default.Face)
    object EncyclopediaArtifacts : NavigationItem("encyclopedia/artifacts", R.string.nav_artifact_sets, Icons.Default.Style)
    object EncyclopediaWeapons : NavigationItem("encyclopedia/weapons", R.string.nav_weapons, Icons.Default.Shield)
    object InventoryArtifacts : NavigationItem("inventory/artifacts", R.string.nav_artifacts, Icons.Default.Backpack)
    object InventoryWeapons : NavigationItem("inventory/weapons", R.string.nav_weapons, Icons.Default.Backpack)
    object Settings : NavigationItem("settings", R.string.nav_settings, Icons.Default.Settings)
}