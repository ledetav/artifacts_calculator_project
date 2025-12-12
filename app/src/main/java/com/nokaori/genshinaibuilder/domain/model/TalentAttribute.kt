package com.nokaori.genshinaibuilder.domain.model

data class TalentAttribute(
    val label: String, // Название, например "1-Hit DMG"
    val values: List<Float> // Значения по уровням: [49.1, 53.1, ...]
)