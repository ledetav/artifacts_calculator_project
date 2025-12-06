package com.nokaori.genshinaibuilder.domain.model

import androidx.compose.ui.graphics.Color

enum class Element(val color: Color) {
    ANEMO(Color(0xFF0359697)),
    CRYO(Color(0x46822B4)),
    DENDRO(Color(0xFF608a00)),
    ELECTRO(Color(0xFF945DC4)),
    GEO(Color(0xFFDEBD6C)),
    HYDRO(Color(0xFF00BFFF)),
    PYRO(Color(0xFFEC4923))
}

data class Character(
    val id: Int,
    val name: string,
    val element: Element,
    val rarity: Int, // 4 или 5
    val isOwned: Boolean = false // Заглушка, есть ли у пользователя
)