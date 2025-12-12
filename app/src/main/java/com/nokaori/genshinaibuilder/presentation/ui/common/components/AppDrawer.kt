package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape // <--- ВАЖНЫЙ ИМПОРТ
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R
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

        ExpandableDrawerGroup(
            title = stringResource(R.string.nav_group_encyclopedia),
            isInitiallyExpanded = true,
            items = listOf(
                NavigationItem.Characters,
                NavigationItem.EncyclopediaArtifacts,
                NavigationItem.EncyclopediaWeapons
            ),
            currentItemRoute = currentItemRoute,
            onItemClick = onItemClick
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 0.dp))

        ExpandableDrawerGroup(
            title = stringResource(R.string.nav_group_inventory),
            isInitiallyExpanded = true,
            items = listOf(
                NavigationItem.InventoryArtifacts,
                NavigationItem.InventoryWeapons
            ),
            currentItemRoute = currentItemRoute,
            onItemClick = onItemClick
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 0.dp))

        NavigationDrawerItem(
            icon = { Icon(NavigationItem.Settings.icon, contentDescription = null) },
            label = { Text(stringResource(NavigationItem.Settings.titleResId)) },
            selected = currentItemRoute == NavigationItem.Settings.route,
            onClick = { onItemClick(NavigationItem.Settings) },
            
            shape = RectangleShape,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
    }
}

@Composable
fun ExpandableDrawerGroup(
    title: String,
    items: List<NavigationItem>,
    currentItemRoute: String?,
    isInitiallyExpanded: Boolean = false,
    onItemClick: (NavigationItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(isInitiallyExpanded) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.titleResId)) },
                        selected = item.route == currentItemRoute,
                        onClick = { onItemClick(item) },
                        
                        shape = RectangleShape,
                        modifier = Modifier
                            .padding(horizontal = 0.dp)
                            .padding(start = 0.dp),
                            
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    }
}