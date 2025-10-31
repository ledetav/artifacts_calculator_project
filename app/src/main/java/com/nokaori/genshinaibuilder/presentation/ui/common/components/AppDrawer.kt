package com.nokaori.genshinaibuilder.presentation.ui.common.components

import android.content.ClipData
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.presentation.ui.navigation.NavigationItem

@Composable
fun AppDrawer(
    items: List<NavigationItem>,
    currentItemRoute: String?,
    onItemClick: (NavigationItem) -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.title) },
                selected = item.route == currentItemRoute,
                onClick = {
                    onItemClick(item)
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}