package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nokaori.genshinaibuilder.presentation.ui.common.Orientation

@Composable
fun <T> SingleSelectToggleButtonGroup(
    title: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit
) {
    BaseToggleButtonGroup(
        title = title,
        items = items,
        modifier = modifier,
        orientation = orientation,
        onItemClick = onItemSelect
    ) { item ->
        val isSelected = (item == selectedItem)

        itemContent(item, isSelected)
    }
}