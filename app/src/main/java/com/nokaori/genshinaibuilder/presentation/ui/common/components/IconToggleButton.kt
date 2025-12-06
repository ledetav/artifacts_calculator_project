package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconToggleButton(
    onClick: () -> Unit,
    isSelected: Boolean,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant

    val iconColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor
            )
        }
    }
}