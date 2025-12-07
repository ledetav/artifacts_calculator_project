package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun IconToggleButton(
    onClick: () -> Unit,
    isSelected: Boolean,
    painter: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    activeContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    inactiveContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val backgroundColor = if (isSelected) activeColor else inactiveContainerColor
    val iconTint = if (isSelected) activeContentColor else inactiveContentColor

    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = iconTint
            )
        }
    }
}