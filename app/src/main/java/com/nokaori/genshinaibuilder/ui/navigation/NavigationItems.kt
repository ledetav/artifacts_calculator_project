package com.nokaori.genshinaibuilder.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Artifacts : NavigationItem("artifacts", "Артефакты", Icons.Default.Style)
    object Weapons : NavigationItem("weapons", "Оружие", Icons.Default.Shield)
    object Characters : NavigationItem("characters", "Персонажи", Icons.Default.Face)
    object Builds : NavigationItem("builds", "Билды", Icons.Default.Build)
    object Settings : NavigationItem("settings", "Настройки", Icons.Default.Settings)
}