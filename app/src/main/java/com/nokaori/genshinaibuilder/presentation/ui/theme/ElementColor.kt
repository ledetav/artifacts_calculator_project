package com.nokaori.genshinaibuilder.presentation.ui.theme

import androidx.compose.ui.graphics.Color
import com.nokaori.genshinaibuilder.domain.model.Element

fun getElementColor(element: Element): Color {
    return when (element) {
        Element.PYRO -> Color(0xFFFF7F50) // Coral Red
        Element.HYDRO -> Color(0xFF00BFFF) // Deep Sky Blue
        Element.ANEMO -> Color(0xFF40E0D0) // Turquoise
        Element.ELECTRO -> Color(0xFF9370DB) // Medium Purple
        Element.DENDRO -> Color(0xFF32CD32) // Lime Green
        Element.CRYO -> Color(0xFFA0E6FF) // Light Blue
        Element.GEO -> Color(0xFFDAA520) // Goldenrod
    }
}