package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.presentation.ui.navigation.NavigationItem

@Composable
fun AppDrawer(
    currentItemRoute: String?,
    onItemClick: (NavigationItem) -> Unit
) {
    ModalDrawerSheet(
        drawerShape = RectangleShape
    ) {
        Spacer(Modifier.height(12.dp))

        val mainItems = listOf(
            NavigationItem.Encyclopedia,
            NavigationItem.Characters,
            NavigationItem.Artifacts,
            NavigationItem.Weapons,
            NavigationItem.Builds
        )

        Column(modifier = Modifier.fillMaxHeight()) {
            mainItems.forEach { item ->
                DrawerItem(item, currentItemRoute, onItemClick)
            }

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider()
            DrawerItem(NavigationItem.Settings, currentItemRoute, onItemClick)
            
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun DrawerItem(
    item: NavigationItem,
    currentItemRoute: String?,
    onClick: (NavigationItem) -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(item.icon, contentDescription = null) },
        label = { Text(stringResource(item.titleResId)) },
        selected = item.route == currentItemRoute,
        onClick = { onClick(item) },
        shape = RectangleShape,
        modifier = Modifier.padding(horizontal = 0.dp)
    )
}