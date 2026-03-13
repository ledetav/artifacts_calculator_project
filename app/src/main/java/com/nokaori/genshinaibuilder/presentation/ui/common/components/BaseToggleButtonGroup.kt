package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.presentation.ui.common.Orientation

@Composable
fun <T> BaseToggleButtonGroup(
    title: String,
    items: List<T>,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    onItemClick: ((T) -> Unit)? = null,
    itemContent: @Composable (item: T) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when (orientation) {
            Orientation.HORIZONTAL -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = horizontalArrangement
                ) {
                    items.forEach { item ->
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clickable(enabled = onItemClick != null) {
                                    onItemClick?.invoke(item)
                                }
                        ) {
                            itemContent(item)
                        }
                    }
                }
            }

            Orientation.VERTICAL -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = verticalArrangement
                ) {
                    items.forEach { item ->
                        Box(
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clickable(enabled = onItemClick != null) {
                                    onItemClick?.invoke(item)
                                }
                        ){
                            itemContent(item)
                        }
                    }
                }
            }
        }
    }
}