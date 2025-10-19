package com.nokaori.genshinaibuilder.ui.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> SingleSelectToggleButtonGroup(
    title: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit
) {
    BaseToggleButtonGroup(
        title = title,
        items = items,
        modifier = modifier
    ) { item ->
        val isSelected = (item == selectedItem)

        itemContent(item, isSelected)
    }
}