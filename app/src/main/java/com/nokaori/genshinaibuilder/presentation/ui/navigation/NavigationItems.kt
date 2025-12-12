package com.nokaori.genshinaibuilder.presentation.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.vector.ImageVector
import com.nokaori.genshinaibuilder.R

sealed class NavigationItem(
    val route: String,
    @StringRes val titleResId: Int,
    val icon: ImageVector
) {
    object Encyclopedia : NavigationItem("encyclopedia", R.string.nav_group_encyclopedia, Icons.Default.MenuBook)
    object Characters : NavigationItem("characters", R.string.nav_characters, Icons.Default.Face)
    object Artifacts : NavigationItem("inventory/artifacts", R.string.nav_artifacts, Icons.Default.Backpack)
    object Weapons : NavigationItem("inventory/weapons", R.string.nav_weapons, Icons.Default.Shield)
    object Builds : NavigationItem("builds", R.string.nav_builds, Icons.Default.Build)
    object Settings : NavigationItem("settings", R.string.nav_settings, Icons.Default.Settings)
}