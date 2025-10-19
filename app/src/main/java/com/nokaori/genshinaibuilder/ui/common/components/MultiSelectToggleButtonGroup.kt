package com.nokaori.genshinaibuilder.ui.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nokaori.genshinaibuilder.ui.common.Orientation

@Composable
fun <T> MultiSelectToggleButtonGroup(
    title: String,
    items: List<T>,
    selectedItems: Set<T>,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit
) {
    BaseToggleButtonGroup(
        title = title,
        items = items,
        modifier = modifier,
        orientation = orientation
    ) { item ->
        val isSelected = item in selectedItems

        itemContent(item, isSelected)
    }
}