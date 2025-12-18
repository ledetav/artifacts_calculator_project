package com.nokaori.genshinaibuilder.domain.model

data class CharacterTalent(
    val name: String,
    val description: String,
    val iconUrl: String,
    val type: TalentType,
    val attributes: List<TalentAttribute>
)

data class CharacterConstellation(
    val order: Int,
    val name: String,
    val description: String,
    val iconUrl: String
)