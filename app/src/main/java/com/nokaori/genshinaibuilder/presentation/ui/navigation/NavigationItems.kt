package com.nokaori.genshinaibuilder.presentation.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
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
    object Artifacts : NavigationItem("artifacts", R.string.nav_artifacts, Icons.Default.Style)
    object Weapons : NavigationItem("weapons", R.string.nav_weapons, Icons.Default.Shield)
    object Characters : NavigationItem("characters", R.string.nav_characters, Icons.Default.Face)
    object Builds : NavigationItem("builds", R.string.nav_builds, Icons.Default.Build)
    object Settings : NavigationItem("settings", R.string.nav_settings, Icons.Default.Settings)
}