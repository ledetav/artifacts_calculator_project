package com.nokaori.genshinaibuilder.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector

data class ArtifactSet(
    val name: String,
    val icon: ImageVector = Icons.Default.Style // Заглушка
)